package pl.dawid0604.mplayer.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordRequest(
        @NotNull(message = "Password cannot be null")
        @Size(min = 6, max = 64, message = "Password must be between 6 and 64 characters")
        String password) { }
