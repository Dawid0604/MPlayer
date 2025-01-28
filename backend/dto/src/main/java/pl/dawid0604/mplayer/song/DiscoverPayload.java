package pl.dawid0604.mplayer.song;

import java.util.List;

public record DiscoverPayload(String searchedText, List<String> genres, List<String> moods,
                              int pageNumber, int pageSize) {
}
