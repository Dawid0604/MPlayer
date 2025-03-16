package pl.dawid0604.mplayer.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotNull(message = "Username cannot be null")
        @Size(min = 6, max = 64, message = "Username must be between 6 and 64 characters")
        String username,

        @NotNull(message = "Nickname cannot be null")
        @Size(min = 6, max = 32, message = "Nickname must be between 6 and 32 characters")
        String nickname,

        @NotNull(message = "Password cannot be null")
        @Size(min = 6, max = 64, message = "Password must be between 6 and 64 characters")
        String password) { }
