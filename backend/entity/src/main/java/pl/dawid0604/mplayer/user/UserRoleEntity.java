package pl.dawid0604.mplayer.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.dawid0604.mplayer.EntityBase;

import java.util.List;

@Entity
@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UserRoles")
@EqualsAndHashCode(callSuper = true)
public class UserRoleEntity extends EntityBase {

    @Column(name = "Name")
    private String name;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "role", orphanRemoval = true)
    private List<UserEntity> users;

    public UserRoleEntity(final long id, final String name) {
        super(id);
        this.name = name;
    }

    public UserRoleEntity(final String name) {
        this.name = name;
    }
}
