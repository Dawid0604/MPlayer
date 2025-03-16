package pl.dawid0604.mplayer.playlist;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.dawid0604.mplayer.tools.DateFormatter.getCurrentDate;

@ExtendWith(MockitoExtension.class)
class PlaylistDaoServiceImplTest {
    @Mock private PlaylistRepository playlistRepository;
    @InjectMocks private PlaylistDaoServiceImpl playlistDaoService;

    @Test
    void shouldFindUserPlaylists() {
        // Given
        long userId = 1;

        given(playlistRepository.findUserPlaylists(userId))
                .willReturn(List.of(new PlaylistEntity()));

        // When
        var result = playlistDaoService.findUserPlaylists(userId);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetDetailsById() {
        // Given
        long playlistId = 1;
        var foundPlaylist = PlaylistEntity.builder()
                                          .encryptedId("anyEncryptedId")
                                          .name("anyName")
                                          .createdDate(getCurrentDate().minusDays(1))
                                          .position(0)
                                          .build();

        var foundSongs = List.of(
                new Object[] { "anyEncryptedId#1", "anyTitle#1", "anyThumbnailPath#1", "anySoundLink#1", "anyAuthor#1,anyAuthor#2", 0 },
                new Object[] { "anyEncryptedId#2", "anyTitle#2", "anyThumbnailPath#2", "anySoundLink#2", "anyAuthor#3", 1 }
        );

        given(playlistRepository.findPlaylistSongs(playlistId))
                .willReturn(foundSongs);

        given(playlistRepository.findDetailsById(eq(playlistId)))
                .willReturn(Optional.of(foundPlaylist));

        // When
        var result = playlistDaoService.getDetailsById(playlistId);

        // Then
        assertEquals(2, result.getSongs().size());
        assertTrue(() -> result.getEncryptedId().equals(foundPlaylist.getEncryptedId()) &&
                         result.getName().equals(foundPlaylist.getName())               &&
                         result.getPosition() == foundPlaylist.getPosition()            &&
                         result.getCreatedDate().equals(foundPlaylist.getCreatedDate()));

        assertTrue(() -> {
            var firstSong = result.getSongs().get(0).getSong();
            var secondSong = result.getSongs().get(1).getSong();
            var firstSongAuthors = ((String) foundSongs.get(0)[4]).split(",");
            var secondSongAuthors = ((String) foundSongs.get(1)[4]).split(",");

            boolean firstSongAuthorsMatches = firstSong.getAuthors().get(0).getName().equals(firstSongAuthors[0]) &&
                                              firstSong.getAuthors().get(1).getName().equals(firstSongAuthors[1]);

            boolean secondSongAuthorsMatches = secondSong.getAuthors().get(0).getName().equals(secondSongAuthors[0]);

            return firstSong.getEncryptedId().equals("anyEncryptedId#1") &&
                   secondSong.getEncryptedId().equals("anyEncryptedId#2") &&
                   firstSongAuthorsMatches && secondSongAuthorsMatches &&
                   firstSongAuthors.length == 2 && secondSongAuthors.length == 1;
        });
    }

    @Test
    void shouldCountSongsByPlaylistId() {
        // Given
        long playlistId = 1;

        // When
        // Then
        assertEquals(0, playlistDaoService.countSongsByPlaylistId(playlistId));
        verify(playlistRepository).countSongsByPlaylistId(eq(playlistId));
    }

    @Test
    void shouldIncreaseSongPosition() {
        // Given
        long playlistId = 1;
        long songId = 1;

        given(playlistRepository.findSongWithNeighbourSongs(eq(playlistId), eq(songId)))
                .willReturn(List.of(new Object[] { 1L, 0 }, new Object[] { 2L, 1 }));

        // When
        // Then
        playlistDaoService.increaseSongPosition(playlistId, songId);
        verify(playlistRepository).findSongWithNeighbourSongs(eq(playlistId), eq(songId));
        verify(playlistRepository).swapSongsPosition(eq(playlistId), eq(List.of(List.of(1L, 1), List.of(2L, 0))));
    }

    @Test
    void shouldDecreaseSongPosition() {
        // Given
        long playlistId = 1;
        long songId = 1;

        given(playlistRepository.findSongWithNeighbourSongs(eq(playlistId), eq(songId)))
                .willReturn(List.of(new Object[] { 1L, 0 }, new Object[] { 2L, 1 }));

        // When
        // Then
        playlistDaoService.decreaseSongPosition(playlistId, songId);
        verify(playlistRepository).findSongWithNeighbourSongs(eq(playlistId), eq(songId));
        verify(playlistRepository).swapSongsPosition(eq(playlistId), eq(List.of(List.of(1L, 1), List.of(2L, 0))));
    }

    @Test
    void shouldDeleteSong() {
        // Given
        long playlistId = 1;
        long songId = 1;

        given(playlistRepository.findSongPosition(eq(playlistId), eq(songId)))
                .willReturn(Optional.of(0));

        // When
        // Then
        playlistDaoService.deleteSong(playlistId, songId);
        verify(playlistRepository).deleteSong(eq(playlistId), eq(songId));
        verify(playlistRepository).correctSongsPosition(eq(playlistId), eq(0));
    }

    @Test
    void shouldIncreasePlaylistPosition() {
        // Given
        long playlistId = 1;

        given(playlistRepository.findPlaylistWithNeighbourPlaylists(eq(playlistId)))
                .willReturn(List.of(new Object[] { 1L, 0 }, new Object[] { 2L, 1 }));

        // When
        playlistDaoService.increasePlaylistPosition(playlistId);

        // Then
        verify(playlistRepository).swapPlaylistsPosition(eq(List.of(List.of(1L, 1), List.of(2L, 0))));
    }

    @Test
    void shouldDecreasePlaylistPosition() {
        // Given
        long playlistId = 1;

        given(playlistRepository.findPlaylistWithNeighbourPlaylists(eq(playlistId)))
                .willReturn(List.of(new Object[] { 1L, 0 }, new Object[] { 2L, 1 }));

        // When
        playlistDaoService.decreasePlaylistPosition(playlistId);

        // Then
        verify(playlistRepository).swapPlaylistsPosition(eq(List.of(List.of(1L, 1), List.of(2L, 0))));
    }

    @Test
    void shouldDeletePlaylist() {
        // Given
        long playlistId = 1;
        long userId = 1;

        given(playlistRepository.findPlaylistPosition(eq(playlistId)))
                .willReturn(Optional.of(0));

        // When
        // Then
        playlistDaoService.deletePlaylist(playlistId, userId);
        verify(playlistRepository).deletePlaylist(eq(playlistId));
        verify(playlistRepository).correctPlaylistsPosition(eq(userId), eq(0));
    }

    @Test
    void shouldRenamePlaylist() {
        // Given
        long playlistId = 1;
        String newName = "newName";

        // When
        // Then
        playlistDaoService.renamePlaylist(playlistId, newName);
        verify(playlistRepository).renamePlaylist(eq(playlistId), eq(newName));
    }

    @Test
    void shouldExistsById() {
        // Given
        long playlistId = 1;

        // When
        // Then
        playlistDaoService.existsById(playlistId);
        verify(playlistRepository).existsById(eq(playlistId));
    }

    @Test
    void shouldPlaylistSongExistsById() {
        // Given
        long playlistId = 1;
        long songId = 1;

        // When
        // Then
        playlistDaoService.playlistSongExistsById(playlistId, songId);
        verify(playlistRepository).playlistSongExistsById(eq(playlistId), eq(songId));
    }

    @Test
    void shouldPlaylistNameExistsByUserId() {
        // Given
        long userId = 1;
        String playlistName = "playlistName";

        // When
        // Then
        playlistDaoService.playlistNameExistsByUserId(userId, playlistName);
        verify(playlistRepository).playlistNameExistsByUserId(eq(userId), eq(playlistName));
    }

    @Test
    void shouldGetNexUserPlaylistPosition() {
        // Given
        long userId = 1;

        // When
        // Then
        playlistDaoService.getLastUserPlaylistPosition(userId);
        verify(playlistRepository).findLastPlaylistPosition(eq(userId));
    }

    @Test
    void shouldSave() {
        // Given
        PlaylistEntity playlist = new PlaylistEntity();

        // When
        // Then
        playlistDaoService.save(playlist);
        verify(playlistRepository).save(eq(playlist));
    }

    @Test
    void shouldAddSongToPlaylist() {
        // Given
        long playlistId = 1;
        long songId = 1;

        // When
        // Then
        playlistDaoService.addSongToPlaylist(playlistId, songId);
        verify(playlistRepository).savePlaylistSongPair(eq(playlistId), eq(songId));
    }
}