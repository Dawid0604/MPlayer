package pl.dawid0604.mplayer.playlist;

import jakarta.persistence.*;
import lombok.*;
import pl.dawid0604.mplayer.EntityBase;
import pl.dawid0604.mplayer.user.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter(PRIVATE)
@Table(name = "Playlists")
@EqualsAndHashCode(callSuper = true)
public class PlaylistEntity extends EntityBase {

    @Column(name = "Name")
    private String name;

    @EqualsAndHashCode.Exclude
    @Column(name = "CreatedDate")
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "UserId")
    @EqualsAndHashCode.Exclude
    private UserEntity user;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "playlist", orphanRemoval = true)
    private List<PlaylistSongsLinksEntity> songs;

    @SuppressWarnings("unused")
    public PlaylistEntity(final String encryptedId, final String name, final LocalDateTime createdDate) {
        super(encryptedId);
        this.name = name;
        this.createdDate = createdDate;
    }

    public void setSongs(final List<PlaylistSongsLinksEntity> songs) {
        if(isEmpty(this.songs)) {
            this.songs = songs;
        }
    }
}
