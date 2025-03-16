package pl.dawid0604.mplayer.song;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.dawid0604.mplayer.encryption.EncryptionService;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;
import pl.dawid0604.mplayer.tools.DateFormatter;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SongRestServiceImplTest {
    @Mock private SongDaoService songDaoService;
    @Mock private EncryptionService encryptionService;
    @InjectMocks private SongRestServiceImpl songRestService;

    @Test
    void shouldFindWelcomeSongs() {
        // Given
        var foundPopularSongs = List.of(
                new SongEntity("encryptedId#1", "title#1", "thumbnailPath#1", "soundLink#1", List.of(
                        new SongAuthorEntity("authorName#1"),
                        new SongAuthorEntity("authorName#2")
                )),

                new SongEntity("encryptedId#2", "title#2", "thumbnailPath#2", "soundLink#2", List.of(
                        new SongAuthorEntity("authorName#1")
                ))
        );

        var foundRecentSongs = List.of(
                new SongEntity("encryptedId#3", "title#3", "thumbnailPath#3", "soundLink#3", List.of(
                        new SongAuthorEntity("authorName#2"),
                        new SongAuthorEntity("authorName#3")
                )),

                new SongEntity("encryptedId#4", "title#4", "thumbnailPath#4", "soundLink#4", List.of(
                        new SongAuthorEntity("authorName#2")
                ))
        );

        given(songDaoService.findWelcomePopularSongs())
                .willReturn(foundPopularSongs);

        given(songDaoService.findWelcomeRecentSongReleases())
                .willReturn(foundRecentSongs);

        // When
        var result = songRestService.findWelcomeSongs();

        // Then
        assertEquals(foundPopularSongs.size(), result.popular().size());
        assertEquals(foundRecentSongs.size(), result.recentReleases().size());
        assertWelcomeSong(foundPopularSongs.get(0), result.popular().get(0));
        assertWelcomeSong(foundPopularSongs.get(1), result.popular().get(1));
        assertWelcomeSong(foundRecentSongs.get(0), result.recentReleases().get(0));
        assertWelcomeSong(foundRecentSongs.get(1), result.recentReleases().get(1));
    }

    @Test
    void shouldHandleSongListening() {
        // Given
        String encryptedId = "encryptedId";
        long songId = 1;

        given(encryptionService.decryptId(eq(encryptedId)))
                .willReturn(songId);

        given(songDaoService.existsById(eq(songId)))
                .willReturn(true);

        // When
        // Then
        songRestService.handleSongListening(encryptedId);
        verify(songDaoService).handleSongListening(eq(songId));
    }

    @Test
    void shouldNotHandleSongListeningWhenSongDoesNotExists() {
        // Given
        String encryptedId = "encryptedId";
        long songId = 1;

        given(encryptionService.decryptId(eq(encryptedId)))
                .willReturn(songId);

        // When
        // Then
        assertThat(catchThrowable(() -> songRestService.handleSongListening(encryptedId)))
                                                       .isInstanceOf(ResourceNotFoundException.class);

        verify(songDaoService, never()).handleSongListening(anyLong());
        verify(songDaoService).existsById(eq(songId));
    }

    @Test
    void shouldDiscover() {
        // Given
        var payload = new DiscoverPayload("text", List.of("genre#1", "genre#2"), List.of("mood#1"), 1, 2);
        var pageable = PageRequest.of(payload.pageNumber() - 1, payload.pageSize());

        var foundSongs = List.of(
                new SongEntity("encryptedId#1", "title#1", "thumbnailPath#1", "soundLink#1", LocalDate.of(2022, 1, 1),
                               List.of(
                                       SongAuthorEntity.builder()
                                                       .name("authorName#1")
                                                       .encryptedId("authorEncryptedId#1")
                                                       .build(),

                                       SongAuthorEntity.builder()
                                                       .name("authorName#2")
                                                       .encryptedId("authorEncryptedId#2")
                                                       .build()
                               ),
                               List.of(
                                       SongMoodEntity.builder()
                                               .name("moodName#1")
                                               .encryptedId("moodEncryptedId#1")
                                               .color("moodColor#1")
                                               .build()
                               ),
                               List.of(
                                       SongGenreEntity.builder()
                                                      .name("genreName#1")
                                                      .encryptedId("genreEncryptedId#1")
                                                      .color("genreColor#1")
                                                      .build()
                               )),

                new SongEntity("encryptedId#2", "title#2", "thumbnailPath#2", "soundLink#2", LocalDate.of(2025, 4, 3),
                               List.of(
                                       SongAuthorEntity.builder()
                                                       .name("authorName#2")
                                                       .encryptedId("authorEncryptedId#2")
                                                       .build()
                               ),
                               List.of(
                                       SongMoodEntity.builder()
                                                     .name("moodName#1")
                                                     .encryptedId("moodEncryptedId#1")
                                                     .color("moodColor#1")
                                                     .build(),

                                       SongMoodEntity.builder()
                                                     .name("moodName#2")
                                                     .encryptedId("moodEncryptedId#2")
                                                     .color("moodColor#2")
                                                     .build()
                               ),
                               List.of(
                                       SongGenreEntity.builder()
                                                      .name("genreName#1")
                                                      .encryptedId("genreEncryptedId#1")
                                                      .color("genreColor#1")
                                                      .build()
                               ))
        );

        given(songDaoService.discover(eq(payload.searchedText()), eq(payload.genres()), eq(payload.moods()),
                                      eq(payload.pageNumber()), eq(payload.pageSize())))

                .willReturn(new PageImpl<>(foundSongs, pageable, foundSongs.size()));

        // When
        var result = songRestService.discover(payload);

        // Then
        assertEquals(foundSongs.size(), result.songs().size());
        assertDiscoverSong(foundSongs.get(0), result.songs().get(0));
        assertDiscoverSong(foundSongs.get(1), result.songs().get(1));
        assertEquals(0, result.pageable().pageNumber());
        assertEquals(2, result.pageable().pageSize());
        assertEquals(1, result.pageable().totalPages());
        assertEquals(2, result.pageable().totalElements());
        assertFalse(result.pageable().hasPrevious());
        assertFalse(result.pageable().hasNext());
        assertTrue(result.pageable().first());
        assertTrue(result.pageable().last());
    }

    private static void assertWelcomeSong(final SongEntity expectedSong, final SongDTO song) {
        var expectedSongAuthors = expectedSong.getAuthors()
                                               .stream()
                                               .map(SongAuthorEntity::getName)
                                               .toList();

        assertEquals(expectedSong.getEncryptedId(), song.encryptedId());
        assertEquals(expectedSong.getTitle(), song.title());
        assertEquals(expectedSong.getThumbnailPath(), song.thumbnailPath());
        assertEquals(expectedSong.getSoundLink(), song.soundLink());
        assertEquals(expectedSongAuthors, song.authors());
    }

    private static void assertDiscoverSong(final SongEntity expectedSong, final DiscoverSongsDTO.SongDTO song) {
        var expectedSongAuthors = expectedSong.getAuthors()
                                               .stream()
                                               .map(SongAuthorEntity::getName)
                                               .toList();

        var expectedSongMoods = expectedSong.getMoods()
                                            .stream()
                                            .map(_mood -> new SongMoodDTO(_mood.getEncryptedId(), _mood.getName(), _mood.getColor()))
                                            .toList();

        var expectedSongGenres = expectedSong.getGenres()
                                             .stream()
                                             .map(_genre -> new SongGenreDTO(_genre.getEncryptedId(), _genre.getName(), _genre.getColor()))
                                             .toList();

        assertEquals(expectedSong.getEncryptedId(), song.encryptedId());
        assertEquals(expectedSong.getTitle(), song.title());
        assertEquals(expectedSong.getThumbnailPath(), song.thumbnailPath());
        assertEquals(expectedSong.getSoundLink(), song.soundLink());
        assertEquals(DateFormatter.withDateFormat(expectedSong.getReleaseDate()), song.releaseDate());
        assertEquals(expectedSongAuthors, song.authors());
        assertEquals(expectedSongMoods, song.moods());
        assertEquals(expectedSongGenres, song.genres());
    }
}