package pl.dawid0604.mplayer.song;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SongGenreDaoServiceImplTest {
    @Mock SongGenreRepository songGenreRepository;
    @InjectMocks private SongGenreDaoServiceImpl songGenreDaoService;

    @Test
    void shouldFindAll() {
        // Given
        // When
        // Then
        songGenreDaoService.findAll();
        verify(songGenreRepository).findAllGenres();
    }
}