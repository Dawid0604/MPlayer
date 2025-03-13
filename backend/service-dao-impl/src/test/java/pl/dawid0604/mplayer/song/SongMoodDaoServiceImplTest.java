package pl.dawid0604.mplayer.song;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SongMoodDaoServiceImplTest {
    @Mock private SongMoodRepository songMoodRepository;
    @InjectMocks private SongMoodDaoServiceImpl songMoodDaoService;

    @Test
    void shouldFindAll() {
        // Given
        // When
        // Then
        songMoodDaoService.findAll();
        verify(songMoodRepository).findAllMoods();
    }
}