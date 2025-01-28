package pl.dawid0604.mplayer.playlist;

import jakarta.persistence.*;
import lombok.*;
import pl.dawid0604.mplayer.EntityBase;
import pl.dawid0604.mplayer.song.SongEntity;
import pl.dawid0604.mplayer.user.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

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

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @JoinTable(name = "PlaylistsSongsLinks",
               joinColumns = @JoinColumn(name = "PlaylistId"),
               inverseJoinColumns = @JoinColumn(name = "SongId"))
    private List<SongEntity> songs;
}
