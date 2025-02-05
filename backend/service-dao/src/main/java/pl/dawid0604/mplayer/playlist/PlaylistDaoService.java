package pl.dawid0604.mplayer.playlist;

import java.util.List;

public interface PlaylistDaoService {
    List<PlaylistEntity> findUserPlaylists(long loggedUserId);

    PlaylistEntity getDetailsById(long playlistId);

    long countSongsByPlaylistId(long playlistId);
}
