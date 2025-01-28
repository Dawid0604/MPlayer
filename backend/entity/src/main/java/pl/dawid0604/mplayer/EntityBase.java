package pl.dawid0604.mplayer;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Getter
@MappedSuperclass
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntityBase {
    @Id
    @Setter(PRIVATE)
    @Column(name = "Id")
    protected Long id;

    @Setter
    @Column(name = "EncryptedId")
    protected String encryptedId;

    public EntityBase(final String encryptedId) {
        this.encryptedId = encryptedId;
    }
}
