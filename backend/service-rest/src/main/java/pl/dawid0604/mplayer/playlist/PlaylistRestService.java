package pl.dawid0604.mplayer.playlist;

import java.util.List;

public interface PlaylistRestService {
    List<PlaylistDTO> findUserPlaylists();

    PlaylistDetailsDTO findPlaylistDetails(String playlistId);

    void increaseSongPosition(String playlistId, String songId);

    void decreaseSongPosition(String playlistId, String songId);

    void deleteSong(String playlistId, String songId);

    void increasePlaylistPosition(String playlistId);

    void decreasePlaylistPosition(String playlistId);

    void deletePlaylist(String playlistId);

    void renamePlaylist(String playlistId, String name);
}
