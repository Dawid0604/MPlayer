package pl.dawid0604.mplayer.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 6, max = 64, message = "Username must be between 6 and 64 characters")
        String username,

        @NotBlank(message = "Nickname cannot be blank")
        @Size(min = 6, max = 32, message = "Nickname must be between 6 and 32 characters")
        String nickname,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 64, message = "Password must be between 6 and 64 characters")
        String password) { }
