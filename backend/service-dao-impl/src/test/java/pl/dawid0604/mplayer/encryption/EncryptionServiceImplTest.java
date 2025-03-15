package pl.dawid0604.mplayer.encryption;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SpringBootTestApplicationContext.class)
class EncryptionServiceImplTest {

    @Autowired
    private EncryptionService encryptionService;

    @Test
    void shouldDecryptId() {
        // Given
        long userId = 1L;
        String encryptedId = encryptionService.encryptUserId(userId);

        // When
        // Then
        assertEquals(userId, encryptionService.decryptId(encryptedId));
    }

    @Test
    void shouldEncryptUserId() {
        // Given
        long userId = 1;

        // When
        String encryptedId = encryptionService.encryptUserId(userId);

        // Then
        assertNotNull(encryptedId);
        assertEquals(userId, encryptionService.decryptId(encryptedId));
    }

    @Test
    void shouldEncryptUserRoleId() {
        // Given
        long userRoleId = 1;

        // When
        String encryptedId = encryptionService.encryptUserRoleId(userRoleId);

        // Then
        assertNotNull(encryptedId);
        assertEquals(userRoleId, encryptionService.decryptId(encryptedId));
    }

    @Test
    void shouldEncryptPlaylistId() {
        // Given
        long playlistId = 1;

        // When
        String encryptedId = encryptionService.encryptPlaylistId(playlistId);

        // Then
        assertNotNull(encryptedId);
        assertEquals(playlistId, encryptionService.decryptId(encryptedId));
    }

    @Test
    void shouldEncryptSongId() {
        // Given
        long songId = 1;

        // When
        String encryptedId = encryptionService.encryptSongId(songId);

        // Then
        assertNotNull(encryptedId);
        assertEquals(songId, encryptionService.decryptId(encryptedId));
    }
    
    @Test
    void shouldEncryptSongAuthorId() {
        // Given
        long songAuthorId = 1;

        // When
        String encryptedId = encryptionService.encryptSongAuthorId(songAuthorId);

        // Then
        assertNotNull(encryptedId);
        assertEquals(songAuthorId, encryptionService.decryptId(encryptedId));
    }
    
    @Test
    void shouldEncryptSongGenreId() {
        // Given
        long songGenreId = 1;

        // When
        String encryptedId = encryptionService.encryptSongGenreId(songGenreId);

        // Then
        assertNotNull(encryptedId);
        assertEquals(songGenreId, encryptionService.decryptId(encryptedId));
    }
    
    @Test
    void shouldEncryptSongMoodId() {
        // Given
        long songMoodId = 1;

        // When
        String encryptedId = encryptionService.encryptSongMoodId(songMoodId);

        // Then
        assertNotNull(encryptedId);
        assertEquals(songMoodId, encryptionService.decryptId(encryptedId));
    }
}