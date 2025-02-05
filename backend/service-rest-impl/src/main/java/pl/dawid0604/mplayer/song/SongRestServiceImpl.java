package pl.dawid0604.mplayer.song;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dawid0604.mplayer.encryption.EncryptionService;
import pl.dawid0604.mplayer.tools.DateFormatter;

@Service
@RequiredArgsConstructor
class SongRestServiceImpl implements SongRestService {
    private final SongDaoService songDaoService;
    private final EncryptionService encryptionService;

    @Override
    public WelcomeSongsDTO findWelcomeSongs() {
        var popularSongs = songDaoService.findWelcomePopularSongs()
                                         .stream()
                                         .map(SongRestServiceImpl::map)
                                         .toList();

        var recentSongsReleases = songDaoService.findWelcomeRecentSongsReleases()
                                                .stream()
                                                .map(SongRestServiceImpl::map)
                                                .toList();

        return new WelcomeSongsDTO(popularSongs, recentSongsReleases);
    }

    @Override
    public void handleSongListening(final String encryptedId) {
        songDaoService.handleSongListening(encryptionService.decryptId(encryptedId));
    }

    @Override
    public DiscoverSongsDTO discover(final DiscoverPayload payload) {
        var result = songDaoService.discover(payload.searchedText(), payload.genres(), payload.moods(),
                                             payload.pageNumber(), payload.pageSize());

        var songs = result.getContent()
                          .stream()
                          .map(SongRestServiceImpl::mapDiscoverSong)
                          .toList();

        var pageableDTO = new DiscoverSongsDTO.Pageable(result.getNumber(), payload.pageSize(), result.getTotalPages(),
                                                        result.getNumberOfElements(), result.hasPrevious(), result.hasNext(),
                                                        result.isFirst(), result.isLast());

        return new DiscoverSongsDTO(pageableDTO, songs);
    }

    private static SongDTO map(final SongEntity songEntity) {
        var authors = songEntity.getAuthors()
                                .stream()
                                .map(SongAuthorEntity::getName)
                                .toList();

        return new SongDTO(songEntity.getEncryptedId(), songEntity.getTitle(), authors,
                           songEntity.getThumbnailPath(), songEntity.getSoundLink());
    }

    private static DiscoverSongsDTO.SongDTO mapDiscoverSong(final SongEntity songEntity) {
        var authors = songEntity.getAuthors()
                                .stream()
                                .map(SongAuthorEntity::getName)
                                .toList();

        var genres = songEntity.getGenres()
                               .stream()
                               .map(_genre -> new SongGenreDTO(_genre.getEncryptedId(), _genre.getName(), _genre.getColor()))
                               .toList();

        var moods = songEntity.getMoods()
                              .stream()
                              .map(_mood -> new SongMoodDTO(_mood.getEncryptedId(), _mood.getName(), _mood.getColor()))
                              .toList();

        return new DiscoverSongsDTO.SongDTO(songEntity.getEncryptedId(), songEntity.getTitle(), authors, genres, moods,
                                         songEntity.getThumbnailPath(), DateFormatter.withDateFormat(songEntity.getReleaseDate()),
                                         songEntity.getSoundLink());
    }
}
