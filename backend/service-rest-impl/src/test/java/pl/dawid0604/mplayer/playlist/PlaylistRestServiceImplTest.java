package pl.dawid0604.mplayer.playlist;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dawid0604.mplayer.encryption.EncryptionService;
import pl.dawid0604.mplayer.exception.ResourceExistException;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;
import pl.dawid0604.mplayer.song.SongDTO;
import pl.dawid0604.mplayer.song.SongDaoService;
import pl.dawid0604.mplayer.song.SongEntity;
import pl.dawid0604.mplayer.user.UserRestService;

import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.*;
import static pl.dawid0604.mplayer.tools.DateFormatter.getCurrentDate;
import static pl.dawid0604.mplayer.tools.DateFormatter.withDateFormat;

@ExtendWith(MockitoExtension.class)
class PlaylistRestServiceImplTest {
    @Mock private PlaylistDaoService playlistDaoService;
    @Mock private UserRestService userRestService;
    @Mock private SongDaoService songDaoService;
    @Mock private EncryptionService encryptionService;
    @InjectMocks private PlaylistRestServiceImpl playlistRestService;

    @Test
    void shouldFindUserPlaylists() {
        // Given
        long userId = 1;
        long firstPlaylistId = 2L;
        long secondPlaylistId = 3L;

        long expectedFirstPlaylistNumberOfSongs = 2;
        long expectedSecondPlaylistNumberOfSongs = 5;

        var foundPlaylists = List.of(
                PlaylistEntity.builder()
                              .encryptedId("encryptedId#1")
                              .name("name#1")
                              .createdDate(getCurrentDate().minusDays(1))
                              .position(1)
                              .build(),

                PlaylistEntity.builder()
                              .encryptedId("encryptedId#2")
                              .name("name#2")
                              .createdDate(getCurrentDate().minusDays(2))
                              .position(2)
                              .build()
        );

        given(userRestService.getLoggedUserId())
                .willReturn(userId);

        given(playlistDaoService.findUserPlaylists(eq(userId)))
                .willReturn(foundPlaylists);

        given(playlistDaoService.countSongsByPlaylistId(eq(firstPlaylistId)))
                .willReturn(expectedFirstPlaylistNumberOfSongs);

        given(playlistDaoService.countSongsByPlaylistId(eq(secondPlaylistId)))
                .willReturn(expectedSecondPlaylistNumberOfSongs);

        given(encryptionService.decryptId(eq(foundPlaylists.get(0).getEncryptedId())))
                .willReturn(firstPlaylistId);

        given(encryptionService.decryptId(eq(foundPlaylists.get(1).getEncryptedId())))
                .willReturn(secondPlaylistId);

        // When
        var result = playlistRestService.findUserPlaylists();

        // Then
        assertEquals(foundPlaylists.size(), result.size());
        assertPlaylist(foundPlaylists.get(0), expectedFirstPlaylistNumberOfSongs, result.get(0));
        assertPlaylist(foundPlaylists.get(1), expectedSecondPlaylistNumberOfSongs, result.get(1));
    }

    @Test
    void shouldFindPlaylistDetails() {
        // Given
        String encryptedId = "encryptedId";
        long playlistId = 1;
        long expectedNumberOfSongs = 5;

        var playlistSongs = List.of(
                new PlaylistSongsLinksEntity(new SongEntity("encryptedId#1", "title#1", "thumbnailPath#1", "soundLink#1", List.of()), 0),
                new PlaylistSongsLinksEntity(new SongEntity("encryptedId#2", "title#2", "thumbnailPath#2", "soundLink#2", List.of()), 1)
        );

        var foundPlaylist = PlaylistEntity.builder()
                                          .encryptedId(encryptedId)
                                          .name("name")
                                          .createdDate(getCurrentDate())
                                          .position(1)
                                          .songs(playlistSongs)
                                          .build();

        given(playlistDaoService.getDetailsById(eq(playlistId)))
                .willReturn(foundPlaylist);

        given(playlistDaoService.countSongsByPlaylistId(eq(playlistId)))
                .willReturn(expectedNumberOfSongs);

        given(encryptionService.decryptId(eq(encryptedId)))
                .willReturn(playlistId);

        // When
        var result = playlistRestService.findPlaylistDetails(encryptedId);

        // Then
        assertPlaylistDetails(foundPlaylist, expectedNumberOfSongs, result);
    }

    @Test
    void shouldNotFindPlaylistDetailsWhenNotFound() {
        // Given
        String encryptedId = "encryptedId";
        long playlistId = 1;

        given(playlistDaoService.getDetailsById(eq(playlistId)))
                .willThrow(ResourceNotFoundException.playlistNotFoundException(playlistId));

        given(encryptionService.decryptId(eq(encryptedId)))
                .willReturn(playlistId);

        // When
        // Then
        assertThat(catchThrowable(() -> playlistRestService.findPlaylistDetails(encryptedId)))
                                                           .isInstanceOf(ResourceNotFoundException.class);

        verify(playlistDaoService, never()).countSongsByPlaylistId(anyLong());
    }

    @Test
    void shouldIncreaseSongPosition() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        String songEncryptedId = "songEncryptedId";

        long playlistId = 1;
        long songId = 2;

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        given(encryptionService.decryptId(eq(songEncryptedId)))
                .willReturn(songId);

        // When
        playlistRestService.increaseSongPosition(playlistEncryptedId, songEncryptedId);

        // Then
        verify(playlistDaoService).increaseSongPosition(eq(playlistId), eq(songId));
    }

    @Test
    void shouldDecreaseSongPosition() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        String songEncryptedId = "songEncryptedId";

        long playlistId = 1;
        long songId = 2;

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        given(encryptionService.decryptId(eq(songEncryptedId)))
                .willReturn(songId);

        // When
        playlistRestService.decreaseSongPosition(playlistEncryptedId, songEncryptedId);

        // Then
        verify(playlistDaoService).decreaseSongPosition(eq(playlistId), eq(songId));
    }
    @Test
    void shouldIncreasePlaylistPosition() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        long playlistId = 1;

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        // When
        playlistRestService.increasePlaylistPosition(playlistEncryptedId);

        // Then
        verify(playlistDaoService).increasePlaylistPosition(eq(playlistId));
    }

    @Test
    void shouldDecreasePlaylistPosition() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        long playlistId = 1;

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        // When
        playlistRestService.decreasePlaylistPosition(playlistEncryptedId);

        // Then
        verify(playlistDaoService).decreasePlaylistPosition(eq(playlistId));
    }

    @Test
    void shouldDeleteSong() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        String songEncryptedId = "songEncryptedId";

        long playlistId = 1;
        long songId = 2;

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        given(encryptionService.decryptId(eq(songEncryptedId)))
                .willReturn(songId);

        given(playlistDaoService.playlistSongExistsById(eq(playlistId), eq(songId)))
                .willReturn(true);

        // When
        playlistRestService.deleteSong(playlistEncryptedId, songEncryptedId);

        // Then
        verify(playlistDaoService).deleteSong(eq(playlistId), eq(songId));
    }

    @Test
    void shouldNotDeleteSongAndThrowException() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        String songEncryptedId = "songEncryptedId";

        long playlistId = 1;
        long songId = 2;

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        given(encryptionService.decryptId(eq(songEncryptedId)))
                .willReturn(songId);

        // When
        // Then
        assertThat(catchThrowable(() -> playlistRestService.deleteSong(playlistEncryptedId, songEncryptedId)))
                                                           .isInstanceOf(ResourceNotFoundException.class);

        verify(playlistDaoService, never()).deleteSong(anyLong(), anyLong());
        verify(playlistDaoService).playlistSongExistsById(eq(playlistId), eq(songId));
    }
    @Test
    void shouldDeletePlaylist() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        long playlistId = 1;
        long userId = 2;

        given(userRestService.getLoggedUserId())
                .willReturn(userId);

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        given(playlistDaoService.existsById(eq(playlistId)))
                .willReturn(true);

        // When
        playlistRestService.deletePlaylist(playlistEncryptedId);

        // Then
        verify(playlistDaoService).deletePlaylist(eq(playlistId), eq(userId));
    }

    @Test
    void shouldNotDeletePlaylistAndThrowException() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        long playlistId = 1;

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        // When
        // Then
        assertThat(catchThrowable(() -> playlistRestService.deletePlaylist(playlistEncryptedId)))
                                                           .isInstanceOf(ResourceNotFoundException.class);

        verify(playlistDaoService, never()).deletePlaylist(anyLong(), anyLong());
        verify(userRestService, never()).getLoggedUserId();
        verify(playlistDaoService).existsById(eq(playlistId));
    }

    @Test
    void shouldRenamePlaylist() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        long playlistId = 1;
        String newName = "newName";

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        given(playlistDaoService.existsById(eq(playlistId)))
                .willReturn(true);

        // When
        playlistRestService.renamePlaylist(playlistEncryptedId, newName);

        // Then
        verify(playlistDaoService).renamePlaylist(eq(playlistId), eq(newName));
    }

    @Test
    void shouldNotRenamePlaylistAndThrowException() {
        // Given
        String playlistEncryptedId = "playlistEncryptedId";
        long playlistId = 1;
        String newName = "newName";

        given(encryptionService.decryptId(eq(playlistEncryptedId)))
                .willReturn(playlistId);

        // When
        // Then
        assertThat(catchThrowable(() -> playlistRestService.renamePlaylist(playlistEncryptedId, newName)))
                                                           .isInstanceOf(ResourceNotFoundException.class);

        verify(playlistDaoService, never()).renamePlaylist(eq(playlistId), eq(newName));
        verify(playlistDaoService).existsById(eq(playlistId));
    }

    @Test
    void shouldCreatePlaylist() {
        // Given
        ArgumentCaptor<PlaylistEntity> argumentCaptor = ArgumentCaptor.forClass(PlaylistEntity.class);
        String playlistName = "xyz";
        String encryptedId = "encryptedId";

        long userId = 1;
        int lastPlaylistPosition = 2;

        doAnswer(_inv -> setId(_inv.getArgument(0))).when(playlistDaoService)
                                                      .save(any(PlaylistEntity.class));

        given(userRestService.getLoggedUserId())
                .willReturn(userId);

        given(playlistDaoService.getLastUserPlaylistPosition(eq(userId)))
                .willReturn(lastPlaylistPosition);

        given(encryptionService.encryptPlaylistId(anyLong()))
                .willReturn(encryptedId);

        // When
        playlistRestService.createPlaylist(playlistName);

        // Then
        verify(playlistDaoService).playlistNameExistsByUserId(eq(userId), eq(playlistName));
        verify(playlistDaoService, times(2)).save(argumentCaptor.capture());
        assertNull(argumentCaptor.getAllValues().get(0).getEncryptedId());
        assertEquals(encryptedId, argumentCaptor.getAllValues().get(1).getEncryptedId());
        assertEquals(playlistName, argumentCaptor.getAllValues().get(0).getName());
        assertEquals(playlistName, argumentCaptor.getAllValues().get(1).getName());
        assertEquals(getCurrentDate().truncatedTo(SECONDS), argumentCaptor.getAllValues().get(0).getCreatedDate().truncatedTo(SECONDS));
        assertEquals(lastPlaylistPosition + 1, argumentCaptor.getAllValues().get(0).getPosition());
    }

    @Test
    void shouldNotCreatePlaylistWhenUserHasPlaylistWithGivenName() {
        // Given
        String playlistName = "xyz";
        long userId = 1;

        given(userRestService.getLoggedUserId())
                .willReturn(userId);

        given(playlistDaoService.playlistNameExistsByUserId(eq(userId), eq(playlistName)))
                .willReturn(true);

        // When
        // Then
        assertThat(catchThrowable(() -> playlistRestService.createPlaylist(playlistName)))
                                                           .isInstanceOf(ResourceExistException.class);

        verify(playlistDaoService, never()).save(any(PlaylistEntity.class));
        verify(playlistDaoService, never()).getLastUserPlaylistPosition(anyLong());
        verifyNoInteractions(encryptionService);
    }

    @Test
    void shouldFindPlaylists() {
        // Given
        String encryptedSongId = "encryptedId";

        long songId = 5;
        long userId = 1;
        long firstPlaylistId = 2L;
        long secondPlaylistId = 3L;

        var foundPlaylists = List.of(
                PlaylistEntity.builder()
                              .encryptedId("encryptedId#1")
                              .name("name#1")
                              .createdDate(getCurrentDate().minusDays(1))
                              .position(1)
                              .build(),

                PlaylistEntity.builder()
                              .encryptedId("encryptedId#2")
                              .name("name#2")
                              .createdDate(getCurrentDate().minusDays(2))
                              .position(2)
                              .build()
        );

        given(userRestService.getLoggedUserId())
                .willReturn(userId);

        given(playlistDaoService.findUserPlaylists(eq(userId)))
                .willReturn(foundPlaylists);

        given(encryptionService.decryptId(eq(foundPlaylists.get(0).getEncryptedId())))
                .willReturn(firstPlaylistId);

        given(encryptionService.decryptId(eq(foundPlaylists.get(1).getEncryptedId())))
                .willReturn(secondPlaylistId);

        given(encryptionService.decryptId(eq(encryptedSongId)))
                .willReturn(songId);

        given(playlistDaoService.playlistSongExistsById(eq(firstPlaylistId), eq(songId)))
                .willReturn(true);

        // When
        var result = playlistRestService.findPlaylists(encryptedSongId);

        // Then
        verify(playlistDaoService).playlistSongExistsById(eq(secondPlaylistId), eq(songId));
        assertPlaylistWithSong(foundPlaylists.get(0), true, result.get(0));
        assertPlaylistWithSong(foundPlaylists.get(1), false, result.get(1));
    }

    @Test
    void shouldAddSongToPlaylist() {
        // Given
        long playlistId = 1;
        long songId = 2;

        String encryptedPlaylistId = "encryptedPlaylistId";
        String encryptedSongId = "encryptedSongId";

        given(encryptionService.decryptId(eq(encryptedPlaylistId)))
                .willReturn(playlistId);

        given(encryptionService.decryptId(eq(encryptedSongId)))
                .willReturn(songId);

        given(songDaoService.existsById(eq(songId)))
                .willReturn(true);

        given(playlistDaoService.existsById(eq(playlistId)))
                .willReturn(true);

        // When
        playlistRestService.addSongToPlaylist(encryptedPlaylistId, encryptedSongId);

        // Then
        verify(playlistDaoService).addSongToPlaylist(eq(playlistId), eq(songId));
    }

    @Test
    void shouldNotAddSongToPlaylistWhenSongNotExists() {
        // Given
        long playlistId = 1;
        long songId = 2;

        String encryptedPlaylistId = "encryptedPlaylistId";
        String encryptedSongId = "encryptedSongId";

        given(encryptionService.decryptId(eq(encryptedSongId)))
                .willReturn(songId);

        // When
        // Then
        assertThat(catchThrowable(() -> playlistRestService.addSongToPlaylist(encryptedPlaylistId, encryptedSongId)))
                                                           .isInstanceOf(ResourceNotFoundException.class);

        verify(playlistDaoService, never()).addSongToPlaylist(anyLong(), anyLong());
        verify(encryptionService, never()).decryptId(encryptedPlaylistId);
        verify(playlistDaoService, never()).existsById(eq(playlistId));
        verify(songDaoService).existsById(eq(songId));
    }

    @Test
    void shouldNotAddSongToPlaylistWhenPlaylistNotExists() {
        // Given
        long playlistId = 1;
        long songId = 2;

        String encryptedPlaylistId = "encryptedPlaylistId";
        String encryptedSongId = "encryptedSongId";

        given(encryptionService.decryptId(eq(encryptedPlaylistId)))
                .willReturn(playlistId);

        given(encryptionService.decryptId(eq(encryptedSongId)))
                .willReturn(songId);

        given(songDaoService.existsById(eq(songId)))
                .willReturn(true);

        // When
        // Then
        assertThat(catchThrowable(() -> playlistRestService.addSongToPlaylist(encryptedPlaylistId, encryptedSongId)))
                                                           .isInstanceOf(ResourceNotFoundException.class);

        verify(playlistDaoService, never()).addSongToPlaylist(anyLong(), anyLong());
        verify(songDaoService).existsById(eq(songId));
    }

    private static void assertPlaylistWithSong(final PlaylistEntity playlistEntity, final boolean songExists,
                                               final PlaylistWithSongDTO result) {

        assertEquals(playlistEntity.getEncryptedId(), result.encryptedId());
        assertEquals(playlistEntity.getName(), result.name());
        assertEquals(songExists, result.songIsPresent());
    }

    private static PlaylistEntity setId(final PlaylistEntity playlistEntity) {
        return PlaylistEntity.builder()
                             .id(playlistEntity.getId() == null ? 2L : playlistEntity.getId())
                             .name(playlistEntity.getName())
                             .position(playlistEntity.getPosition())
                             .encryptedId(playlistEntity.getEncryptedId())
                             .createdDate(playlistEntity.getCreatedDate())
                             .build();
    }

    private static void assertPlaylistDetails(final PlaylistEntity expectedEntity, final long expectedNumberOfSongs,
                                              final PlaylistDetailsDTO result) {

        var details = result.playlist();
        assertEquals(expectedEntity.getEncryptedId(), details.encryptedId());
        assertEquals(expectedEntity.getName(), details.name());
        assertEquals(withDateFormat(expectedEntity.getCreatedDate()), details.createdDate());
        assertEquals(expectedNumberOfSongs, details.numberOfSongs());

        var songs = result.songs();
        assertEquals(expectedEntity.getSongs().size(), songs.size());
        assertPlaylistSong(expectedEntity.getSongs().get(0), songs.get(0),0);
        assertPlaylistSong(expectedEntity.getSongs().get(1), songs.get(1),1);
    }

    private static void assertPlaylistSong(final PlaylistSongsLinksEntity expectedEntity, final SongDTO song, final int position) {
        var expectedSong = expectedEntity.getSong();
        assertEquals(expectedSong.getEncryptedId(), song.encryptedId());
        assertEquals(expectedSong.getTitle(), song.title());
        assertEquals(expectedSong.getThumbnailPath(), song.thumbnailPath());
        assertEquals(expectedSong.getSoundLink(), song.soundLink());
        assertEquals(expectedEntity.getPosition(), position);
    }

    private static void assertPlaylist(final PlaylistEntity expectedEntity, final long expectedNumberOfSongs,
                                       final PlaylistDTO result) {

        assertEquals(expectedEntity.getEncryptedId(), result.encryptedId());
        assertEquals(expectedEntity.getName(), result.name());
        assertEquals(withDateFormat(expectedEntity.getCreatedDate()), result.createdDate());
        assertEquals(expectedNumberOfSongs, result.numberOfSongs());
    }
}