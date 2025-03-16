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
class SongMoodRestServiceImplTest {
    @Mock private SongMoodDaoService songMoodDaoService;
    @InjectMocks private SongMoodRestServiceImpl songMoodRestService;

    @Test
    void shouldFindAll() {
        // Given
        var foundMoods = List.of(
                new SongMoodEntity("encryptedId#1", "name#1", "color#1"),
                new SongMoodEntity("encryptedId#2", "name#2", "color#2")
        );

        given(songMoodDaoService.findAll())
                .willReturn(foundMoods);

        // When
        var result = songMoodRestService.findAll();

        // Then
        assertEquals(foundMoods.size(), result.size());
        assertMood(foundMoods.get(0), result.get(0));
        assertMood(foundMoods.get(1), result.get(1));
    }

    private static void assertMood(final SongMoodEntity expectedMood, final SongMoodDTO mood) {
        assertEquals(expectedMood.getEncryptedId(), mood.encryptedId());
        assertEquals(expectedMood.getName(), mood.name());
        assertEquals(expectedMood.getColor(), mood.color());
    }
}