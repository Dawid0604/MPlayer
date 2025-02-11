package pl.dawid0604.mplayer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserRoleDaoServiceImpl implements UserRoleDaoService {
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserRoleEntity> findByName(final String role) {
        return userRoleRepository.findByName(role);
    }
}
