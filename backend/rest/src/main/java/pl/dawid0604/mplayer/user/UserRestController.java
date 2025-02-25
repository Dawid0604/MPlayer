package pl.dawid0604.mplayer.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + "user")
public class UserRestController {
    private final UserRestService userRestService;

    @DeleteMapping
    @ResponseStatus(OK)
    public void deleteAccount() {
        userRestService.delete();
    }

    @GetMapping
    @ResponseStatus(OK)
    public UserDataDTO getLoggedUserData() {
        return userRestService.getLoggedUserData();
    }

    @PatchMapping
    @ResponseStatus(OK)
    public void updatePassword(@RequestBody @Valid final UserUpdatePasswordRequest request) {
        userRestService.updatePassword(request);
    }
}
