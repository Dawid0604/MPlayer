package pl.dawid0604.mplayer.playlist;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.dawid0604.mplayer.EntityBase;
import pl.dawid0604.mplayer.user.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Entity
@SuperBuilder
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

    @Column(name = "Position")
    private int position;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "playlist", orphanRemoval = true)
    private List<PlaylistSongsLinksEntity> songs;

    @SuppressWarnings("unused")
    public PlaylistEntity(final String encryptedId, final String name, final LocalDateTime createdDate,
                          final int position) {

        super(encryptedId);
        this.name = name;
        this.createdDate = createdDate;
        this.position = position;
    }

    public void setSongs(final List<PlaylistSongsLinksEntity> songs) {
        if(isEmpty(this.songs)) {
            this.songs = songs;
        }
    }

    public void setUser(final UserEntity user) {
        if(this.user == null) {
            this.user = user;
        }
    }
}
