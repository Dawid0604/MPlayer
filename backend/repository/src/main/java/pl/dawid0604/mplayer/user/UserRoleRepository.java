package pl.dawid0604.mplayer.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    @Query("""
            SELECT new pl.dawid0604.mplayer.user.UserRoleEntity(r.id, r.name)
            FROM #{#entityName} r
            WHERE r.name = :role
           """)
    Optional<UserRoleEntity> findByName(String role);
}
