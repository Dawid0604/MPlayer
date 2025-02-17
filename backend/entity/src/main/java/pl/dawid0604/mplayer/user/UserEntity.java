package pl.dawid0604.mplayer.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.dawid0604.mplayer.EntityBase;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@SuperBuilder
@Setter(PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users")
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends EntityBase {

    @Setter(PRIVATE)
    @Column(name = "Username")
    private String username;

    @Setter
    @Column(name = "Password")
    private String password;

    @Setter(PRIVATE)
    @Column(name = "Nickname")
    private String nickname;

    @Setter(PRIVATE)
    @EqualsAndHashCode.Exclude
    @Column(name = "CreatedDate")
    private LocalDateTime createdDate;

    @Setter
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "RoleId")
    private UserRoleEntity role;

    public UserEntity(final String username, final String password, final UserRoleEntity role) {
        this.password = password;
        this.role = role;
        this.username = username;
    }

    public UserEntity(final long id) {
        super(id);
    }
}
