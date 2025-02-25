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

    @Override
    public void increaseSongPosition(final long playlistId, final long songId) {
        var songWithNeighbourSongs = playlistRepository.findSongWithNeighbourSongs(playlistId, songId);
        List<List<Number>> swapSongs = (songWithNeighbourSongs.size() == 3) ? List.of(List.of((long) songWithNeighbourSongs.get(1)[0], (int) songWithNeighbourSongs.get(2)[1]),
                                                                                      List.of((long) songWithNeighbourSongs.get(2)[0], (int) songWithNeighbourSongs.get(1)[1]))

                                                                            : List.of(List.of((long) songWithNeighbourSongs.get(0)[0], (int) songWithNeighbourSongs.get(1)[1]),
                                                                                      List.of((long) songWithNeighbourSongs.get(1)[0], (int) songWithNeighbourSongs.get(0)[1]));

        playlistRepository.swapSongsPosition(playlistId, swapSongs);
    }

    @Override
    public void decreaseSongPosition(final long playlistId, final long songId) {
        var songWithNeighbourSongs = playlistRepository.findSongWithNeighbourSongs(playlistId, songId);
        playlistRepository.swapSongsPosition(playlistId, List.of(List.of((long) songWithNeighbourSongs.get(0)[0], (int) songWithNeighbourSongs.get(1)[1]),
                                                                 List.of((long) songWithNeighbourSongs.get(1)[0], (int) songWithNeighbourSongs.get(0)[1])));
    }

    @Override
    @Transactional
    public void deleteSong(final long playlistId, final long songId) {
        var songPosition = playlistRepository.findSongPosition(playlistId, songId)
                                             .orElseThrow();

        playlistRepository.deleteSong(playlistId, songId);
        playlistRepository.correctSongsPosition(playlistId, songPosition);
    }

    @Override
    public void increasePlaylistPosition(final long playlistId) {
        var playlistWithNeighbourPlaylists = playlistRepository.findPlaylistWithNeighbourPlaylists(playlistId);
        List<List<Number>> swapPlaylists = (playlistWithNeighbourPlaylists.size() == 3) ? List.of(List.of((long) playlistWithNeighbourPlaylists.get(1)[0], (int) playlistWithNeighbourPlaylists.get(2)[1]),
                                                                                                  List.of((long) playlistWithNeighbourPlaylists.get(2)[0], (int) playlistWithNeighbourPlaylists.get(1)[1]))

                                                                                        : List.of(List.of((long) playlistWithNeighbourPlaylists.get(0)[0], (int) playlistWithNeighbourPlaylists.get(1)[1]),
                                                                                                  List.of((long) playlistWithNeighbourPlaylists.get(1)[0], (int) playlistWithNeighbourPlaylists.get(0)[1]));

        playlistRepository.swapPlaylistsPosition(swapPlaylists);
    }

    @Override
    public void decreasePlaylistPosition(final long playlistId) {
        var playlistWithNeighbourPlaylists = playlistRepository.findPlaylistWithNeighbourPlaylists(playlistId);
        playlistRepository.swapPlaylistsPosition(List.of(List.of((long) playlistWithNeighbourPlaylists.get(0)[0], (int) playlistWithNeighbourPlaylists.get(1)[1]),
                                                         List.of((long) playlistWithNeighbourPlaylists.get(1)[0], (int) playlistWithNeighbourPlaylists.get(0)[1])));
    }

    @Override
    @Transactional
    public void deletePlaylist(final long playlistId) {
        var playlistPosition = playlistRepository.findPlaylistPosition(playlistId)
                                                 .orElseThrow();

        playlistRepository.deletePlaylist(playlistId);
        playlistRepository.correctPlaylistsPosition(playlistId, playlistPosition);
    }

    @Override
    @Transactional
    public void renamePlaylist(final long playlistId, final String name) {
        playlistRepository.renamePlaylist(playlistId, name);
    }

    @Override
    public boolean existsById(final long playlistId) {
        return playlistRepository.existsById(playlistId);
    }

    @Override
    public boolean playlistSongExistsById(final long playlistId, final long songId) {
        return playlistRepository.playlistSongExistsById(playlistId, songId).isPresent();
    }

    @Override
    public boolean playlistNameExistsByUser(final long userId, final String playlistName) {
        return playlistRepository.playlistNameExistsByUser(userId, playlistName);
    }

    @Override
    public int getNextUserPlaylistPosition(final long userId) {
        return playlistRepository.findLastPlaylistPosition(userId)
                                 .orElse(0);
    }

    @Override
    @Transactional
    public PlaylistEntity save(final PlaylistEntity playlist) {
        return playlistRepository.save(playlist);
    }

    @Override
    @Transactional
    public void addSongToPlaylist(final long playlistId, final long songId) {
        playlistRepository.addSongToPlaylist(playlistId, songId, playlistRepository.findLastPlaylistSongPosition(playlistId));
    }

    private static PlaylistSongsLinksEntity map(final Object[] song) {
        List<SongAuthorEntity> songAuthors = RegexTool.split((String) song[4], COMMA_PATTERN)
                                                      .stream()
                                                      .map(_groupedFields -> RegexTool.split(_groupedFields, COLON_PATTERN))
                                                      .flatMap(List::stream)
                                                      .map(SongAuthorEntity::new)
                                                      .toList();

        var _song = new SongEntity((String) song[0], (String) song[1], (String) song[2], (String) song[3], songAuthors);
        return new PlaylistSongsLinksEntity(_song, (int) song[5]);
    }
}
