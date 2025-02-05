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
}
