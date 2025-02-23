package pl.dawid0604.mplayer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawid0604.mplayer.encryption.EncryptionService;
import pl.dawid0604.mplayer.exception.ResourceExistException;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;

import java.util.Optional;

import static pl.dawid0604.mplayer.tools.DateFormatter.getCurrentDate;

@Service
@RequiredArgsConstructor
class UserRestServiceImpl implements UserRestService {
    private final UserDaoService userDaoService;
    private final UserRoleDaoService userRoleDaoService;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    @Override
    public long getLoggedUserId() {
        return getLoggedUser().flatMap(_username -> userDaoService.findIdByUsername(_username.getUsername()))
                              .orElseThrow();
    }

    @Override
    public String getLoggedUserUsername() {
        return getLoggedUser().map(UserDetails::getUsername)
                              .orElseThrow();
    }

    @Override
    @Transactional
    public void register(final String username, final String password, final String nickname) {
        throwWhenUsernameExists(username);
        throwWhenNicknameExists(nickname);
        UserRoleEntity userRole = userRoleDaoService.findByName("USER")
                                                    .orElseThrow(() -> ResourceNotFoundException.userRoleNotFoundException("USER"));

        UserEntity user = UserEntity.builder()
                                    .username(username)
                                    .nickname(nickname)
                                    .password(passwordEncoder.encode(password))
                                    .role(userRole)
                                    .createdDate(getCurrentDate())
                                    .build();

        user = userDaoService.save(user);
        user.setEncryptedId(encryptionService.encryptUserId(user.getId()));
        userDaoService.save(user);
    }

    private void throwWhenUsernameExists(final String username) throws ResourceExistException {
        if(userDaoService.existsByUsername(username)) {
            throw ResourceExistException.userUsernameException(username);
        }
    }

    private void throwWhenNicknameExists(final String nickname) throws ResourceExistException {
        if(userDaoService.existsByNickname(nickname)) {
            throw ResourceExistException.userNicknameException(nickname);
        }
    }

    private static Optional<UserDetails> getLoggedUser() {
        return Optional.ofNullable((UserDetails) SecurityContextHolder.getContext()
                                                                      .getAuthentication()
                                                                      .getPrincipal());
    }
}
