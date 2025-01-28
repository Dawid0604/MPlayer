package pl.dawid0604.mplayer.encryption;

public interface EncryptionService {
    long decryptId(String encryptedId);

    String encryptUserId(long userId);

    String encryptUserRoleId(long userRoleId);

    String encryptPlaylistId(long playlistId);

    String encryptSongId(long songId);

    String encryptSongAuthorId(long songAuthorId);

    String encryptSongGenreId(long songGenreId);

    String encryptSongMoodId(long songMoodId);
}
