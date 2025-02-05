package pl.dawid0604.mplayer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserRestServiceImpl implements UserRestService {
    private final UserDaoService userDaoService;

    @Override
    public long getLoggedUserId() {
        return getLoggedUser().flatMap(_username -> userDaoService.findIdByUsername(_username.getUsername()))
                              .orElseThrow();
    }

    private static Optional<UserDetails> getLoggedUser() {
        return Optional.ofNullable((UserDetails) SecurityContextHolder.getContext()
                                                                      .getAuthentication()
                                                                      .getPrincipal());
    }
}
