package pl.dawid0604.mplayer.playlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dawid0604.mplayer.encryption.EncryptionService;
import pl.dawid0604.mplayer.song.SongAuthorEntity;
import pl.dawid0604.mplayer.song.SongDTO;
import pl.dawid0604.mplayer.song.SongEntity;
import pl.dawid0604.mplayer.user.UserRestService;

import java.util.Comparator;
import java.util.List;

import static pl.dawid0604.mplayer.tools.DateFormatter.withDateFormat;

@Service
@RequiredArgsConstructor
class PlaylistRestServiceImpl implements PlaylistRestService {
    private final PlaylistDaoService playlistDaoService;
    private final UserRestService userRestService;
    private final EncryptionService encryptionService;

    @Override
    public List<PlaylistDTO> findUserPlaylists() {
        return playlistDaoService.findUserPlaylists(userRestService.getLoggedUserId())
                                 .stream()
                                 .map(this::map)
                                 .toList();
    }

    @Override
    public PlaylistDetailsDTO findPlaylistDetails(final String playlistId) {
        return mapDetails(playlistDaoService.getDetailsById(encryptionService.decryptId(playlistId)));
    }

    @Override
    public void increaseSongPosition(final String playlistId, final String songId) {
        playlistDaoService.increaseSongPosition(encryptionService.decryptId(playlistId), encryptionService.decryptId(songId));
    }

    @Override
    public void decreaseSongPosition(final String playlistId, final String songId) {
        playlistDaoService.decreaseSongPosition(encryptionService.decryptId(playlistId), encryptionService.decryptId(songId));
    }

    @Override
    public void deleteSong(final String playlistId, final String songId) {
        playlistDaoService.deleteSong(encryptionService.decryptId(playlistId), encryptionService.decryptId(songId));
    }

    private PlaylistDTO map(final PlaylistEntity playlistEntity) {
        return new PlaylistDTO(playlistEntity.getEncryptedId(), playlistEntity.getName(), withDateFormat(playlistEntity.getCreatedDate()),
                               playlistDaoService.countSongsByPlaylistId(encryptionService.decryptId(playlistEntity.getEncryptedId())));
    }

    private PlaylistDetailsDTO mapDetails(final PlaylistEntity playlistEntity) {
        return new PlaylistDetailsDTO(map(playlistEntity), playlistEntity.getSongs()
                                                                         .stream()
                                                                         .sorted(Comparator.comparingInt(PlaylistSongsLinksEntity::getPosition))
                                                                         .map(PlaylistSongsLinksEntity::getSong)
                                                                         .map(PlaylistRestServiceImpl::mapSong)
                                                                         .toList());
    }

    private static SongDTO mapSong(final SongEntity songEntity) {
        var authors = songEntity.getAuthors()
                                .stream()
                                .map(SongAuthorEntity::getName)
                                .toList();

        return new SongDTO(songEntity.getEncryptedId(), songEntity.getTitle(), authors,
                           songEntity.getThumbnailPath(), songEntity.getSoundLink());
    }
}
