package pl.dawid0604.mplayer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserDaoServiceImpl implements UserDaoService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByUsername(final String username) {
        return userRepository.findUsernamePasswordRoleByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> findIdByUsername(final String username) {
        return userRepository.findIdByUsername(username);
    }

    @Override
    public boolean existsByUsername(final String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public boolean existsByNickname(final String nickname) {
        return userRepository.existsByNicknameIgnoreCase(nickname);
    }

    @Override
    @Transactional
    public UserEntity save(final UserEntity entity) {
        return userRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteById(final long loggedUserId) {
        userRepository.deleteByIdCustom(loggedUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> findUsernameRoleNicknameById(final long loggedUserId) {
        return userRepository.findUsernameRoleNicknameById(loggedUserId);
    }

    @Override
    @Transactional
    public void updatePassword(final long loggedUserId, final String password) {
        userRepository.updatePassword(loggedUserId, password);
    }
}
