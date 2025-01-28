package pl.dawid0604.mplayer.song;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class SongMoodDaoServiceImpl implements SongMoodDaoService {
    private final SongMoodRepository songMoodRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SongMoodEntity> findAll() {
        return songMoodRepository.findAllMoods();
    }
}
