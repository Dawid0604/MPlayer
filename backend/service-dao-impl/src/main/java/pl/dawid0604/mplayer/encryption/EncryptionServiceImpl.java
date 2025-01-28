package pl.dawid0604.mplayer.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.dawid0604.mplayer.exception.DecryptionException;
import pl.dawid0604.mplayer.exception.EncryptionException;
import pl.dawid0604.mplayer.song.SongGenreRepository;
import pl.dawid0604.mplayer.song.SongMoodRepository;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static pl.dawid0604.mplayer.encryption.EncryptionConstants.*;

@Service
class EncryptionServiceImpl implements EncryptionService {
    private final SecretKeySpec secretKeySpec;

    public EncryptionServiceImpl(@Value("${custom.security.aes.secretKey}") final String secretKey) {
        this.secretKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
    }

    @Override

    public long decryptId(final String encryptedId) {
        return decrypt(encryptedId);
    }

    @Override
    public String encryptUserId(final long userId) {
        return encrypt(appendSuffix(userId, USER_SUFFIX));
    }

    @Override
    public String encryptUserRoleId(final long userRoleId) {
        return encrypt(appendSuffix(userRoleId, USER_ROLE_SUFFIX));
    }

    @Override
    public String encryptPlaylistId(final long playlistId) {
        return encrypt(appendSuffix(playlistId, PLAYLIST_SUFFIX));
    }

    @Override
    public String encryptSongId(final long songId) {
        return encrypt(appendSuffix(songId, SONG_SUFFIX));
    }

    @Override
    public String encryptSongAuthorId(final long songAuthorId) {
        return encrypt(appendSuffix(songAuthorId, SONG_AUTHOR_SUFFIX));
    }

    @Override
    public String encryptSongGenreId(final long songGenreId) {
        return encrypt(appendSuffix(songGenreId, SONG_GENRE_SUFFIX));
    }

    @Override
    public String encryptSongMoodId(final long songMoodId) {
        return encrypt(appendSuffix(songMoodId, SONG_MOOD_SUFFIX));
    }

    private String encrypt(final String preparedId) throws EncryptionException {
        try {
            var cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(ENCRYPT_MODE, secretKeySpec);

            return Base64.getUrlEncoder().encodeToString(cipher.doFinal(preparedId.getBytes()));

        } catch (InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | NoSuchPaddingException    |
                 NoSuchAlgorithmException exception) {

            throw new EncryptionException();
        }
    }

    private long decrypt(final String encryptedId) throws DecryptionException{
        try {
            var cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(DECRYPT_MODE, secretKeySpec);

            return Long.parseLong(removeSuffix(new String(cipher.doFinal(Base64.getUrlDecoder().decode(encryptedId)))));

        } catch (InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException | NoSuchPaddingException    |
                 NoSuchAlgorithmException exception) {

            throw new DecryptionException();
        }
    }

    private static String appendSuffix(final long id, final String suffix) {
        return id + SEPARATOR + suffix;
    }

    private static String removeSuffix(final String decodedId) {
        return decodedId.substring(0, decodedId.indexOf(SEPARATOR));
    }
}
