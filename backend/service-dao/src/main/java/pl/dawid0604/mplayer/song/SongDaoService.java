package pl.dawid0604.mplayer.song;

import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface SongDaoService {
    List<SongEntity> findWelcomePopularSongs();

    List<SongEntity> findWelcomeRecentSongReleases();

    void handleSongListening(long songId);

    PageImpl<SongEntity> discover(String searchedText, List<String> genres,
                                  List<String> moods, int pageNumber, int pageSize);

    boolean existsById(long songId);
}
