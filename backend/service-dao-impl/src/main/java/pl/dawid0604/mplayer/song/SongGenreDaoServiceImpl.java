package pl.dawid0604.mplayer.song;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class SongGenreDaoServiceImpl implements SongGenreDaoService {
    private final SongGenreRepository songGenreRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SongGenreEntity> findAll() {
        return songGenreRepository.findAllGenres();
    }
}
