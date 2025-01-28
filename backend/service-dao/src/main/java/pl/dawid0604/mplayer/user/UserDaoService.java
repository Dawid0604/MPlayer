package pl.dawid0604.mplayer.user;

import java.util.Optional;

public interface UserDaoService {
    Optional<UserEntity> findByUsername(String username);
}
