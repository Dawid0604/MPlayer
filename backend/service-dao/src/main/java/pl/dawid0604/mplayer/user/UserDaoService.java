package pl.dawid0604.mplayer.user;

import java.util.Optional;

public interface UserDaoService {
    Optional<UserEntity> findByUsername(String username);

    Optional<Long> findIdByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    UserEntity save(UserEntity entity);
}
