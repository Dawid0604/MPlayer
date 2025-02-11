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
}
