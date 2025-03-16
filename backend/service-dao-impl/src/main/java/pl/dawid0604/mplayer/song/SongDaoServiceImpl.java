package pl.dawid0604.mplayer.song;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawid0604.mplayer.tools.RegexTool;

import java.sql.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.CollectionUtils.isEmpty;
import static pl.dawid0604.mplayer.tools.RegexTool.*;

@Service
@RequiredArgsConstructor
class SongDaoServiceImpl implements SongDaoService {
    private final SongRepository songRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    private static final String TRUE_EXPRESSION = "TRUE";
    private static final String AND_EXPRESSION = " AND ";
    private static final String OR_EXPRESSION = " OR ";
    private static final String SINGLE_QUOTE = "'";
    private static final String AGAINST_MATCH_EXPRESSION = "MATCH (%s) AGAINST ('*%s*' IN BOOLEAN MODE)";
    private static final Pattern IGNORED_SEARCHED_TEXT_CHARACTERS_PATTERN = Pattern.compile("[<>()~*+\\-'\"]");
    private static final int SEARCHED_TEXT_MIN_LENGTH = 3;

    private static final String DISCOVER_QUERY = """
                SELECT s.EncryptedId, s.Title, s.ThumbnailPath, s.SoundLink, s.ReleaseDate,
                       GROUP_CONCAT(DISTINCT sa.Name ORDER BY sa.Name ASC SEPARATOR ',') AS Authors,
                       GROUP_CONCAT(DISTINCT CONCAT(m.EncryptedId, ':', m.Name, ':', m.color) ORDER BY m.Name ASC SEPARATOR ',') AS Moods,
                       GROUP_CONCAT(DISTINCT CONCAT(g.EncryptedId, ':', g.Name, ':', g.color) ORDER BY g.Name ASC SEPARATOR ',') AS Genres
                FROM Songs as s
                INNER JOIN SongAuthorsLinks as asl ON asl.SongId = s.Id
                INNER JOIN SongAuthors as sa ON asl.AuthorId = sa.Id
                INNER JOIN SongMoodsLinks as msl ON msl.SongId = s.Id
                INNER JOIN SongMoods as m ON msl.MoodId = m.Id
                INNER JOIN SongGenresLinks as gsl ON gsl.SongId = s.Id
                INNER JOIN SongGenres as g ON gsl.GenreId = g.Id
                WHERE ("%1$s" = 'TRUE' OR EXISTS(SELECT 1 FROM Songs as sub_s
                                                 INNER JOIN SongAuthorsLinks AS sub_asl ON sub_asl.SongId = sub_s.Id
                                                 INNER JOIN SongAuthors AS sub_sa ON sub_asl.AuthorId = sub_sa.Id
                                                 WHERE sub_s.Id = s.Id
                                                 AND %1$s)
                      ) AND
                      ('' IN (%2$s) OR (
                        (SELECT COUNT(DISTINCT sub_m.EncryptedId)
                         FROM SongMoodsLinks as sub_msl
                         INNER JOIN SongMoods as sub_m ON sub_msl.MoodId = sub_m.Id
                         WHERE sub_msl.SongId = s.Id AND sub_m.EncryptedId IN (%2$s)) = (SELECT COUNT(*) FROM (SELECT EncryptedId FROM SongMoods WHERE EncryptedId IN (%2$s)) as count_m)
                      )) AND
                      ('' IN (%3$s) OR (
                        (SELECT COUNT(DISTINCT sub_g.EncryptedId)
                         FROM SongGenresLinks as sub_gsl
                         INNER JOIN SongGenres as sub_g ON sub_gsl.GenreId = sub_g.Id
                         WHERE sub_gsl.SongId = s.Id AND sub_g.EncryptedId IN (%3$s)) = (SELECT COUNT(*) FROM (SELECT EncryptedId FROM SongGenres WHERE EncryptedId IN (%3$s)) as count_g)
                      ))
                GROUP BY s.Id
                LIMIT %4$s OFFSET %5$s
            """;

    private static final String COUNT_DISCOVER_QUERY = """
                SELECT COUNT(DISTINCT s.Id)
                FROM Songs as s
                INNER JOIN SongAuthorsLinks as asl ON asl.SongId = s.Id
                INNER JOIN SongAuthors as sa ON asl.AuthorId = sa.Id
                INNER JOIN SongMoodsLinks as msl ON msl.SongId = s.Id
                INNER JOIN SongMoods as m ON msl.MoodId = m.Id
                INNER JOIN SongGenresLinks as gsl ON gsl.SongId = s.Id
                INNER JOIN SongGenres as g ON gsl.GenreId = g.Id
                WHERE ("%1$s" = 'TRUE' OR EXISTS(SELECT 1 FROM Songs as sub_s
                                                 INNER JOIN SongAuthorsLinks AS sub_asl ON sub_asl.SongId = sub_s.Id
                                                 INNER JOIN SongAuthors AS sub_sa ON sub_asl.AuthorId = sub_sa.Id
                                                 WHERE sub_s.Id = s.Id
                                                 AND %1$s)
                      ) AND
                      ('' IN (%2$s) OR (
                        (SELECT COUNT(DISTINCT sub_m.EncryptedId)
                         FROM SongMoodsLinks as sub_msl
                         INNER JOIN SongMoods as sub_m ON sub_msl.MoodId = sub_m.Id
                         WHERE sub_msl.SongId = s.Id AND sub_m.EncryptedId IN (%2$s)) = (SELECT COUNT(*) FROM (SELECT EncryptedId FROM SongMoods WHERE EncryptedId IN (%2$s)) as count_m)
                      )) AND
                      ('' IN (%3$s) OR (
                        (SELECT COUNT(DISTINCT sub_g.EncryptedId)
                         FROM SongGenresLinks as sub_gsl
                         INNER JOIN SongGenres as sub_g ON sub_gsl.GenreId = sub_g.Id
                         WHERE sub_gsl.SongId = s.Id AND sub_g.EncryptedId IN (%3$s)) = (SELECT COUNT(*) FROM (SELECT EncryptedId FROM SongGenres WHERE EncryptedId IN (%3$s)) as count_g)
                      ))
            """;

    private static final String WELCOME_POPULAR_SONGS = """
                SELECT s.EncryptedId, s.Title, s.ThumbnailPath, s.SoundLink,
                       GROUP_CONCAT(DISTINCT sa.Name ORDER BY sa.Name ASC SEPARATOR ',') AS Authors
                FROM Songs as s
                INNER JOIN SongAuthorsLinks as asl ON asl.SongId = s.Id
                INNER JOIN SongAuthors as sa ON asl.AuthorId = sa.Id
                GROUP BY s.Id
                ORDER BY s.NumberOfListens DESC
                LIMIT 6
            """;

    private static final String WELCOME_RECENT_SONGS = """
                SELECT s.EncryptedId, s.Title, s.ThumbnailPath, s.SoundLink,
                       GROUP_CONCAT(DISTINCT sa.Name ORDER BY sa.Name ASC SEPARATOR ',') AS Authors
                FROM Songs as s
                INNER JOIN SongAuthorsLinks as asl ON asl.SongId = s.Id
                INNER JOIN SongAuthors as sa ON asl.AuthorId = sa.Id
                GROUP BY s.Id
                ORDER BY s.releaseDate DESC
                LIMIT 18
            """;

    @Override
    @SuppressWarnings("unchecked")
    public List<SongEntity> findWelcomePopularSongs() {
        return ((List<Object[]>) entityManager.createNativeQuery(WELCOME_POPULAR_SONGS)
                                              .getResultList())
                                              .stream()
                                              .map(SongDaoServiceImpl::map)
                                              .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SongEntity> findWelcomeRecentSongReleases() {
        return ((List<Object[]>) entityManager.createNativeQuery(WELCOME_RECENT_SONGS)
                                              .getResultList())
                                              .stream()
                                              .map(SongDaoServiceImpl::map)
                                              .toList();
    }

    @Override
    public void handleSongListening(final long songId) {
        songRepository.incrementNumberOfListens(songId);
    }

    @Override
    public boolean existsById(final long songId) {
        return songRepository.existsById(songId);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public PageImpl<SongEntity> discover(final String searchedText, final List<String> genres,
                                         final List<String> moods, final int pageNumber, final int pageSize) {

        String parsedMoods = toString(moods);
        String parsedGenres = toString(genres);
        String searchedTextExpression = getSearchedTextExpression(searchedText);

        var songs = ((List<Object[]>) entityManager.createNativeQuery(DISCOVER_QUERY.formatted(searchedTextExpression, parsedMoods, parsedGenres, pageSize, pageNumber * pageSize))
                                                   .getResultList())
                                                   .stream()
                                                   .map(SongDaoServiceImpl::mapDiscoveredSong)
                                                   .toList();

        long totalResults = (long) entityManager.createNativeQuery(COUNT_DISCOVER_QUERY.formatted(searchedTextExpression, parsedMoods, parsedGenres))
                                                .getSingleResult();

        return new PageImpl<>(songs, PageRequest.of(pageNumber, pageSize), totalResults);
    }

    private static String getSearchedTextExpression(final String searchedText) {
        if(searchedTextIsNotValid(searchedText)) {
            return TRUE_EXPRESSION;
        }

        List<String> words = RegexTool.split(searchedText, SPACE_PATTERN);
        StringBuilder expressionBuilder = new StringBuilder();

        for (String word: words) {
            word = IGNORED_SEARCHED_TEXT_CHARACTERS_PATTERN.matcher(word)
                                                           .replaceAll(EMPTY);

            if (isNotBlank(word)) {
                String titleExpression = AGAINST_MATCH_EXPRESSION.formatted("sub_s.Title", word);
                String authorExpression = AGAINST_MATCH_EXPRESSION.formatted("sub_sa.Name", word);
                String expression = "(" + titleExpression + OR_EXPRESSION + authorExpression + ")";

                if (!expressionBuilder.isEmpty()) {
                    expressionBuilder.append(AND_EXPRESSION);
                }

                expressionBuilder.append(expression);
            }
        }

        return expressionBuilder.isEmpty() ? TRUE_EXPRESSION
                                           : expressionBuilder.toString();
    }

    private static boolean searchedTextIsNotValid(final String searchedText) {
        return length(searchedText) < SEARCHED_TEXT_MIN_LENGTH;
    }

    private static String toString(final List<String> list) {
        if(!isEmpty(list)) {
            return list.stream()
                       .map(_elm -> SINGLE_QUOTE + _elm + SINGLE_QUOTE)
                       .collect(Collectors.joining(", "));
        } else {
            return SINGLE_QUOTE + SINGLE_QUOTE;
        }
    }

    private static SongEntity map(final Object[] song) {
        List<SongAuthorEntity> songAuthors = RegexTool.split((String) song[4], COMMA_PATTERN)
                                                      .stream()
                                                      .map(_groupedFields -> RegexTool.split(_groupedFields, COLON_PATTERN))
                                                      .flatMap(List::stream)
                                                      .map(SongAuthorEntity::new)
                                                      .toList();

        return new SongEntity((String) song[0], (String) song[1], (String) song[2], (String) song[3], songAuthors);
    }

    private static SongEntity mapDiscoveredSong(final Object[] song) {
        List<SongAuthorEntity> songAuthors = RegexTool.split((String) song[5], COMMA_PATTERN)
                                                      .stream()
                                                      .map(_groupedFields -> RegexTool.split(_groupedFields, COLON_PATTERN))
                                                      .flatMap(List::stream)
                                                      .map(SongAuthorEntity::new)
                                                      .toList();
        
        List<SongMoodEntity> songMoods = RegexTool.split((String) song[6], COMMA_PATTERN)
                                                  .stream()
                                                  .map(_groupedFields -> RegexTool.split(_groupedFields, COLON_PATTERN))
                                                  .map(_fields -> new SongMoodEntity(_fields.get(0), _fields.get(1), _fields.get(2))).toList();
        
        List<SongGenreEntity> songGenres = RegexTool.split((String) song[7], COMMA_PATTERN)
                                                    .stream()
                                                    .map(_groupedFields -> RegexTool.split(_groupedFields, COLON_PATTERN))
                                                    .map(_fields -> new SongGenreEntity(_fields.get(0), _fields.get(1), _fields.get(2))).toList();

        return new SongEntity((String) song[0], (String) song[1], (String) song[2], (String) song[3],
                              ((Date) song[4]).toLocalDate(), songAuthors, songMoods, songGenres);
    }
}
