package pl.dawid0604.mplayer.song;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SongGenreRestServiceImplTest {
    @Mock private SongGenreDaoService songGenreDaoService;
    @InjectMocks private SongGenreRestServiceImpl songGenreRestService;

    @Test
    void shouldFindAll() {
        // Given
        var foundGenres = List.of(
                new SongGenreEntity("encryptedId#1", "name#1", "color#1"),
                new SongGenreEntity("encryptedId#2", "name#2", "color#2")
        );

        given(songGenreDaoService.findAll())
                .willReturn(foundGenres);

        // When
        var result = songGenreRestService.findAll();

        // Then
        assertEquals(foundGenres.size(), result.size());
        assertGenre(foundGenres.get(0), result.get(0));
        assertGenre(foundGenres.get(1), result.get(1));
    }

    private static void assertGenre(final SongGenreEntity expectedGenre, final SongGenreDTO genre) {
        assertEquals(expectedGenre.getEncryptedId(), genre.encryptedId());
        assertEquals(expectedGenre.getName(), genre.name());
        assertEquals(expectedGenre.getColor(), genre.color());
    }
}