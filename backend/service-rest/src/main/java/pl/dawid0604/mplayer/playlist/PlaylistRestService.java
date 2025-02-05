package pl.dawid0604.mplayer.playlist;

import java.util.List;

public interface PlaylistRestService {
    List<PlaylistDTO> findUserPlaylists();

    PlaylistDetailsDTO findPlaylistDetails(String playlistId);
}
