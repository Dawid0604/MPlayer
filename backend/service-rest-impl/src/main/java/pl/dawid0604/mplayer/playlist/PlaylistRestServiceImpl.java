package pl.dawid0604.mplayer.playlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawid0604.mplayer.encryption.EncryptionService;
import pl.dawid0604.mplayer.exception.ResourceExistException;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;
import pl.dawid0604.mplayer.song.SongAuthorEntity;
import pl.dawid0604.mplayer.song.SongDTO;
import pl.dawid0604.mplayer.song.SongDaoService;
import pl.dawid0604.mplayer.song.SongEntity;
import pl.dawid0604.mplayer.user.UserEntity;
import pl.dawid0604.mplayer.user.UserRestService;

import java.util.Comparator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static pl.dawid0604.mplayer.tools.DateFormatter.getCurrentDate;
import static pl.dawid0604.mplayer.tools.DateFormatter.withDateFormat;

@Service
@RequiredArgsConstructor
class PlaylistRestServiceImpl implements PlaylistRestService {
    private final PlaylistDaoService playlistDaoService;
    private final UserRestService userRestService;
    private final SongDaoService songDaoService;
    private final EncryptionService encryptionService;

    @Override
    public List<PlaylistDTO> findUserPlaylists() {
        return playlistDaoService.findUserPlaylists(userRestService.getLoggedUserId())
                                 .stream()
                                 .sorted(Comparator.comparingInt(PlaylistEntity::getPosition))
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
    public void deleteSong(final String encryptedPlaylistId, final String encryptedSongId) {
        long playlistId = encryptionService.decryptId(encryptedPlaylistId);
        long songId = encryptionService.decryptId(encryptedSongId);

        throwWhenPlaylistSongNotFound(playlistId, songId);
        playlistDaoService.deleteSong(playlistId, songId);
    }

    @Override
    public void increasePlaylistPosition(final String playlistId) {
        playlistDaoService.increasePlaylistPosition(encryptionService.decryptId(playlistId));
    }

    @Override
    public void decreasePlaylistPosition(final String playlistId) {
        playlistDaoService.decreasePlaylistPosition(encryptionService.decryptId(playlistId));
    }

    @Override
    public void deletePlaylist(final String encryptedId) throws ResourceNotFoundException {
        long playlistId = encryptionService.decryptId(encryptedId);

        throwWhenPlaylistNotFound(playlistId);
        playlistDaoService.deletePlaylist(playlistId, userRestService.getLoggedUserId());
    }

    @Override
    public void renamePlaylist(final String encryptedId, final String name) throws ResourceNotFoundException {
        if(isBlank(name)) {
            throw new IllegalArgumentException("Playlist Name cannot be blank");
        }

        long playlistId = encryptionService.decryptId(encryptedId);
        throwWhenPlaylistNotFound(playlistId);
        playlistDaoService.renamePlaylist(playlistId, name);
    }

    @Override
    @Transactional
    public void createPlaylist(final String name) {
        var user = new UserEntity(userRestService.getLoggedUserId());
        throwWhenUserHasPlaylistWithGivenName(name, userRestService.getLoggedUserId());

        PlaylistEntity playlist = PlaylistEntity.builder()
                                                .name(name)
                                                .createdDate(getCurrentDate())
                                                .user(user)
                                                .position(playlistDaoService.getNextUserPlaylistPosition(user.getId()) + 1)
                                                .build();

        playlist = playlistDaoService.save(playlist);
        playlist.setEncryptedId(encryptionService.encryptPlaylistId(playlist.getId()));
        playlistDaoService.save(playlist);
    }

    @Override
    public List<PlaylistWithSongDTO> findPlaylists(final String songId) {
        return playlistDaoService.findUserPlaylists(userRestService.getLoggedUserId())
                                 .stream()
                                 .map(_playlist -> map(_playlist, songId))
                                 .toList();
    }

    @Override
    @Transactional
    public void addSongToPlaylist(final String playlistId, final String songId) {
        throwWhenSongNotFound(encryptionService.decryptId(songId));
        throwWhenPlaylistNotFound(encryptionService.decryptId(playlistId));
        playlistDaoService.addSongToPlaylist(encryptionService.decryptId(playlistId), encryptionService.decryptId(songId));
    }

    private void throwWhenPlaylistNotFound(final long playlistId) throws ResourceNotFoundException {
        if(!playlistDaoService.existsById(playlistId)) {
            throw ResourceNotFoundException.playlistNotFoundException(playlistId);
        }
    }

    private void throwWhenPlaylistSongNotFound(final long playlistId, final long songId) throws ResourceNotFoundException {
        if(!playlistDaoService.playlistSongExistsById(playlistId, songId)) {
            throw ResourceNotFoundException.playlistSongNotFoundException(playlistId, songId);
        }
    }

    private void throwWhenSongNotFound(final long songId) throws ResourceNotFoundException {
        if(!songDaoService.existsById(songId)) {
            throw ResourceNotFoundException.songNotFoundException(songId);
        }
    }

    private void throwWhenUserHasPlaylistWithGivenName(final String playlistName, final long userId) throws ResourceNotFoundException {
        if(playlistDaoService.playlistNameExistsByUserId(userId, playlistName)) {
            throw ResourceExistException.userHasPlaylistWithGivenName(playlistName);
        }
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

    private PlaylistWithSongDTO map(final PlaylistEntity playlist, final String songId) {
        return new PlaylistWithSongDTO(playlist.getEncryptedId(), playlist.getName(), playlistDaoService.playlistSongExistsById(encryptionService.decryptId(playlist.getEncryptedId()),
                                                                                                                                encryptionService.decryptId(songId)));
    }
}
