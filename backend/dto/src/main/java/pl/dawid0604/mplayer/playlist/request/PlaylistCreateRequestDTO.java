package pl.dawid0604.mplayer.playlist.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlaylistCreateRequestDTO(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 64, message = "Name must be between 6 and 64 characters")
        String name) { }
