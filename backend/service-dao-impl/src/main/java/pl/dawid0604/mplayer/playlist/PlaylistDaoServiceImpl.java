package pl.dawid0604.mplayer.playlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawid0604.mplayer.song.SongAuthorEntity;
import pl.dawid0604.mplayer.song.SongEntity;
import pl.dawid0604.mplayer.tools.RegexTool;

import java.util.List;

import static pl.dawid0604.mplayer.tools.RegexTool.COLON_PATTERN;
import static pl.dawid0604.mplayer.tools.RegexTool.COMMA_PATTERN;

@Service
@RequiredArgsConstructor
class PlaylistDaoServiceImpl implements PlaylistDaoService {
    private final PlaylistRepository playlistRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistEntity> findUserPlaylists(final long loggedUserId) {
        return playlistRepository.findUserPlaylists(loggedUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistEntity getDetailsById(final long playlistId) {
        var playlist = playlistRepository.findDetailsById(playlistId)
                                         .orElseThrow();

        var songs = playlistRepository.findPlaylistSongs(playlistId)
                                      .stream()
                                      .map(PlaylistDaoServiceImpl::map)
                                      .toList();

        playlist.setSongs(songs);
        return playlist;
    }

    @Override
    public long countSongsByPlaylistId(final long playlistId) {
        return playlistRepository.countSongsByPlaylistId(playlistId);
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
}
