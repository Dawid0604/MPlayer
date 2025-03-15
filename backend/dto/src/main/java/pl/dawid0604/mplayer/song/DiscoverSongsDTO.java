package pl.dawid0604.mplayer.song;

import java.util.List;

import static java.util.Collections.emptyList;

public record DiscoverSongsDTO(Pageable pageable, List<SongDTO> songs) {

    record SongDTO(String encryptedId, String title, List<String> authors,
                   List<SongGenreDTO> genres, List<SongMoodDTO> moods, String thumbnailPath,
                   String releaseDate, String soundLink) {

        public SongDTO() {
            this("", "", emptyList(), emptyList(), emptyList(), "", "", "");
        }
    }

    record Pageable(int pageNumber, int pageSize, int totalPages, int totalElements,
                    boolean hasPrevious, boolean hasNext, boolean first, boolean last) {

        public Pageable() {
            this(0, 0, 0, 0, false, false, false, false);
        }
    }
}
