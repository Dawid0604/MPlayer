package pl.dawid0604.mplayer.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordRequest(
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 64, message = "Password must be between 6 and 64 characters")
        String password) { }
