package pl.dawid0604.mplayer.playlist;

import java.util.List;

public interface PlaylistDaoService {
    List<PlaylistEntity> findUserPlaylists(long loggedUserId);

    PlaylistEntity getDetailsById(long playlistId);

    long countSongsByPlaylistId(long playlistId);

    void increaseSongPosition(long playlistId, long songId);

    void decreaseSongPosition(long playlistId, long songId);

    void deleteSong(long playlistId, long songId);
    
    void increasePlaylistPosition(long playlistId);

    void decreasePlaylistPosition(long playlistId);

    void deletePlaylist(long playlistId);

    void renamePlaylist(long playlistId, String name);

    boolean existsById(long playlistId);

    boolean playlistSongExistsById(long playlistId, long songId);

    PlaylistEntity save(PlaylistEntity playlist);

    boolean playlistNameExistsByUser(long userId, String playlistName);

    int getNextUserPlaylistPosition(long userId);

    void addSongToPlaylist(long playlistId, long songId);
}
