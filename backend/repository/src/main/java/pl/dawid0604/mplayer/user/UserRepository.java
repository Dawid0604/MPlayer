package pl.dawid0604.mplayer.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("""
            SELECT new pl.dawid0604.mplayer.user.UserEntity(u.username, u.password,
                   new pl.dawid0604.mplayer.user.UserRoleEntity(r.name))
            FROM #{#entityName} u
            LEFT JOIN u.role r
            WHERE u.username = :username
           """)
    Optional<UserEntity> findUsernamePasswordRoleByUsername(String username);
}
