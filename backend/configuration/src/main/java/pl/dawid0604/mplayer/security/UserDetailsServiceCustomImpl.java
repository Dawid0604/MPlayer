package pl.dawid0604.mplayer.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.dawid0604.mplayer.user.UserDaoService;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceCustomImpl implements UserDetailsService {
    private final UserDaoService userService;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userService.findByUsername(username)
                          .map(_user -> User.builder()
                                            .username(_user.getUsername())
                                            .password(_user.getPassword())
                                            .authorities(_user.getRole().getName())
                                            .build())
                          .orElseThrow();
    }
}
