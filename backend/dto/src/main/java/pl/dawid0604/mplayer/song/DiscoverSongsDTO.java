package pl.dawid0604.mplayer.song;

import java.util.List;

public record DiscoverSongsDTO(Pageable pageable, List<SongDTO> songs) {

    record SongDTO(String encryptedId, String title, List<String> authors,
                   List<SongGenreDTO> genres, List<SongMoodDTO> moods, String thumbnailPath,
                   String releaseDate, String soundLink) { }

    record Pageable(int pageNumber, int pageSize, int totalPages, int totalElements,
                    boolean hasPrevious, boolean hasNext, boolean first, boolean last) { }
}
