package pl.dawid0604.mplayer.user;

import java.util.Optional;

public interface UserRoleDaoService {
    Optional<UserRoleEntity> findByName(String role);
}
