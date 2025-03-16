package pl.dawid0604.mplayer.song;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class SongMoodRestServiceImpl implements SongMoodRestService {
    private final SongMoodDaoService songMoodDaoService;

    @Override
    public List<SongMoodDTO> findAll() {
        return songMoodDaoService.findAll()
                                 .stream()
                                 .map(SongMoodRestServiceImpl::map)
                                 .toList();
    }

    private static SongMoodDTO map(final SongMoodEntity songMoodEntity) {
        return new SongMoodDTO(songMoodEntity.getEncryptedId(), songMoodEntity.getName(), songMoodEntity.getColor());
    }
}
