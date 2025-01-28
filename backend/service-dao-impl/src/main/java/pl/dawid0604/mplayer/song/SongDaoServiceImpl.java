package pl.dawid0604.mplayer.song;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
class SongDaoServiceImpl implements SongDaoService {
    private final SongRepository songRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SongEntity> findWelcomePopularSongs() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> songCriteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<SongEntity> songRootQuery = songCriteriaQuery.from(SongEntity.class);

        songCriteriaQuery.multiselect(songRootQuery.get("encryptedId"));
        songCriteriaQuery.orderBy(criteriaBuilder.desc(songRootQuery.get("numberOfListens")));

        var foundSongsEncryptedIds = entityManager.createQuery(songCriteriaQuery)
                                                  .setMaxResults(6)
                                                  .getResultList();

        CriteriaQuery<Object[]> detailsQuery = criteriaBuilder.createQuery(Object[].class);
        Root<SongEntity> detailsRoot = detailsQuery.from(SongEntity.class);
        Join<SongEntity, SongAuthorEntity> authorsJoin = detailsRoot.join("authors");

        detailsQuery.distinct(true);
        detailsQuery.where(detailsRoot.get("encryptedId").in(foundSongsEncryptedIds));
        detailsQuery.multiselect(detailsRoot.get("encryptedId"), detailsRoot.get("title"), detailsRoot.get("thumbnailPath"),
                                 detailsRoot.get("soundLink"), authorsJoin.get("name"), detailsRoot.get("releaseDate"),
                                 detailsRoot.get("numberOfListens"));

        return entityManager.createQuery(detailsQuery)
                            .getResultList()
                            .stream()
                            .collect(groupingBy(_fields -> _fields[0]))
                            .values()
                            .stream()
                            .map(this::map)
                            .sorted(Comparator.comparing(SongEntity::getNumberOfListens).reversed())
                            .toList();
    }

    @Override
    public List<SongEntity> findWelcomeRecentSongsReleases() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> songCriteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<SongEntity> songRootQuery = songCriteriaQuery.from(SongEntity.class);

        songCriteriaQuery.multiselect(songRootQuery.get("encryptedId"));
        songCriteriaQuery.orderBy(criteriaBuilder.desc(songRootQuery.get("releaseDate")));

        var foundSongsEncryptedIds = entityManager.createQuery(songCriteriaQuery)
                                                  .setMaxResults(18)
                                                  .getResultList();

        CriteriaQuery<Object[]> detailsQuery = criteriaBuilder.createQuery(Object[].class);
        Root<SongEntity> detailsRoot = detailsQuery.from(SongEntity.class);
        Join<SongEntity, SongAuthorEntity> authorsJoin = detailsRoot.join("authors");

        detailsQuery.distinct(true);
        detailsQuery.where(detailsRoot.get("encryptedId").in(foundSongsEncryptedIds));
        detailsQuery.multiselect(detailsRoot.get("encryptedId"), detailsRoot.get("title"), detailsRoot.get("thumbnailPath"),
                                 detailsRoot.get("soundLink"), authorsJoin.get("name"), detailsRoot.get("releaseDate"),
                                 detailsRoot.get("numberOfListens"));

        return entityManager.createQuery(detailsQuery)
                            .getResultList()
                            .stream()
                            .collect(groupingBy(_fields -> _fields[0]))
                            .values()
                            .stream()
                            .map(this::map)
                            .sorted(Comparator.comparing(SongEntity::getReleaseDate).reversed())
                            .toList();
    }

    @Override
    public void handleSongListening(final long songId) {
        songRepository.incrementNumberOfListening(songId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageImpl<SongEntity> discover(final String searchedText, final List<String> genres,
                                         final List<String> moods, final int pageNumber, final int pageSize) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<SongEntity> countRootQuery = countCriteriaQuery.from(SongEntity.class);

        countCriteriaQuery.multiselect(criteriaBuilder.count(countRootQuery.get("encryptedId")));
        long totalResults = entityManager.createQuery(countCriteriaQuery)
                                         .getSingleResult();

        CriteriaQuery<String> songCriteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<SongEntity> songRootQuery = songCriteriaQuery.from(SongEntity.class);

        songCriteriaQuery.multiselect(songRootQuery.get("encryptedId"));
        var foundSongsEncryptedIds = entityManager.createQuery(songCriteriaQuery)
                                                  .setFirstResult(pageNumber * pageSize)
                                                  .setMaxResults(pageSize)
                                                  .getResultList();

        CriteriaQuery<Object[]> detailsQuery = criteriaBuilder.createQuery(Object[].class);
        Root<SongEntity> detailsRoot = detailsQuery.from(SongEntity.class);
        Join<SongEntity, SongAuthorEntity> authorsJoin = detailsRoot.join("authors");
        Join<SongEntity, SongMoodEntity> moodsJoin = detailsRoot.join("moods");
        Join<SongEntity, SongGenreEntity> genresJoin = detailsRoot.join("genres");

        detailsQuery.distinct(true);
        detailsQuery.multiselect(detailsRoot.get("encryptedId"), detailsRoot.get("title"), detailsRoot.get("thumbnailPath"),
                                 detailsRoot.get("soundLink"), detailsRoot.get("releaseDate"), authorsJoin.get("name"),
                                 moodsJoin.get("encryptedId"), moodsJoin.get("name"), moodsJoin.get("color"), genresJoin.get("encryptedId"),
                                 genresJoin.get("name"), genresJoin.get("color"));

        detailsQuery.where(detailsRoot.get("encryptedId").in(foundSongsEncryptedIds));
        var songs = entityManager.createQuery(detailsQuery)
                                 .getResultList()
                                 .stream()
                                 .collect(groupingBy(_fields -> _fields[0]))
                                 .values()
                                 .stream()
                                 .map(this::mapDiscoveredSong)
                                 .toList();

        return new PageImpl<>(songs, PageRequest.of(pageNumber, pageSize), totalResults);
    }

    private SongEntity map(final List<Object[]> songWithAuthors) {
        Object[] song = songWithAuthors.get(0);
        List<SongAuthorEntity> songAuthors = new ArrayList<>();

        if(songWithAuthors.size() > 1) {
            for (var songWithAuthor: songWithAuthors) {
                songAuthors.add(new SongAuthorEntity((String) songWithAuthor[4]));
            }

        } else {
            songAuthors.add(new SongAuthorEntity((String) song[4]));
        }

        return new SongEntity((String) song[0], (String) song[1], (String) song[2], (String) song[3], songAuthors,
                              (LocalDate) song[5], (int) song[6]);
    }


    private SongEntity mapDiscoveredSong(final List<Object[]> groupedSong) {
        Object[] song = groupedSong.get(0);
        Set<SongAuthorEntity> songAuthors = new HashSet<>();
        Set<SongMoodEntity> songMoods = new HashSet<>();
        Set<SongGenreEntity> songGenres = new HashSet<>();

        for (var _song: groupedSong) {
            String tempValue;

            if((tempValue = (String) _song[5]) != null) {
                songAuthors.add(new SongAuthorEntity(tempValue));
            }

            if((tempValue = (String) _song[9]) != null) {
                songGenres.add(new SongGenreEntity(tempValue, (String) _song[10], (String) _song[11]));
            }

            if((tempValue = (String) _song[6]) != null) {
                songMoods.add(new SongMoodEntity(tempValue, (String) _song[7], (String) _song[8]));
            }
        }

        return new SongEntity((String) song[0], (String) song[1], (String) song[2], (String) song[3],
                              (LocalDate) song[4], songAuthors.stream().toList(), songMoods.stream().toList(),
                              songGenres.stream().toList());
    }
}
