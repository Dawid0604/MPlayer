package pl.dawid0604.mplayer;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@MappedSuperclass
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntityBase {
    @Id
    @Setter(PRIVATE)
    @Column(name = "Id")
    @GeneratedValue(strategy = IDENTITY)
    protected Long id;

    @Setter
    @Column(name = "EncryptedId")
    protected String encryptedId;

    public EntityBase(final String encryptedId) {
        this.encryptedId = encryptedId;
    }

    public EntityBase(final long id) {
        this.id = id;
    }
}
