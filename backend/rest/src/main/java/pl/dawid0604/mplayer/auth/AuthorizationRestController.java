package pl.dawid0604.mplayer.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.dawid0604.mplayer.user.UserRegistrationRequest;
import pl.dawid0604.mplayer.user.UserRestService;

import static org.springframework.http.HttpStatus.OK;
import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + "auth")
public class AuthorizationRestController {
    private final UserRestService userRestService;

    @ResponseStatus(OK)
    @PostMapping("/register")
    public void register(@RequestBody @Valid final UserRegistrationRequest request) {
        userRestService.register(request.username(), request.password(), request.nickname());
    }
}
