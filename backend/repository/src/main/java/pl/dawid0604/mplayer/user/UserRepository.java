package pl.dawid0604.mplayer.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Query("""
            SELECT new pl.dawid0604.mplayer.user.UserEntity(u.username,
                   new pl.dawid0604.mplayer.user.UserRoleEntity(r.name), u.nickname)
            FROM #{#entityName} u
            LEFT JOIN u.role r
            WHERE u.id = :userId
           """)
    Optional<UserEntity> findUsernameRoleNicknameById(long userId);

    @Query("SELECT u.id FROM #{#entityName} u WHERE u.username = :username")
    Optional<Long> findIdByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByNicknameIgnoreCase(String nickname);

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} u WHERE u.id = :loggedUserId")
    void deleteByIdCustom(long loggedUserId);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} u SET u.password = :password WHERE u.id = :loggedUserId")
    void updatePassword(long loggedUserId, String password);
}
