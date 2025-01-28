package pl.dawid0604.mplayer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserRestServiceImpl implements UserRestService {

    private static UserDetails getLoggedUser() {
        return (UserDetails) SecurityContextHolder.getContext()
                                                  .getAuthentication()
                                                  .getPrincipal();
    }
}
