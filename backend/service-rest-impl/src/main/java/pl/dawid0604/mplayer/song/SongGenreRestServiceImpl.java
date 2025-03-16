package pl.dawid0604.mplayer.song;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class SongGenreRestServiceImpl implements SongGenreRestService {
    private final SongGenreDaoService songGenreDaoService;

    @Override
    public List<SongGenreDTO> findAll() {
        return songGenreDaoService.findAll()
                                  .stream()
                                  .map(SongGenreRestServiceImpl::map)
                                  .toList();
    }

    private static SongGenreDTO map(final SongGenreEntity songGenreEntity) {
        return new SongGenreDTO(songGenreEntity.getEncryptedId(), songGenreEntity.getName(), songGenreEntity.getColor());
    }
}
