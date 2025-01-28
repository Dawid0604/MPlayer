package pl.dawid0604.mplayer.song;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dawid0604.mplayer.encryption.EncryptionService;

import java.util.List;

@Service
@RequiredArgsConstructor
class SongMoodRestServiceImpl implements SongMoodRestService {
    private final SongMoodDaoService songMoodDaoService;
    private final EncryptionService encryptionService;

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
