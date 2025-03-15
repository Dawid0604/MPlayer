package pl.dawid0604.mplayer.song;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DiscoverPayload(
        @Size(min = 3, message = "Search text must be at least 3 characters")
        String searchedText,

        List<String> genres,

        List<String> moods,

        @Min(value = 0, message = "Page number must be greater or equal to 0")
        int pageNumber,

        @Min(value = 1, message = "Page size must be greater than 0")
        int pageSize) { }
