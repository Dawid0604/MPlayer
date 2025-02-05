package pl.dawid0604.mplayer.playlist;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dawid0604.mplayer.song.SongEntity;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PlaylistsSongsLinks")
public class PlaylistSongsLinksEntity {

    @EmbeddedId
    private PlaylistSongsLinksPair id;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "PlaylistId")
    private PlaylistEntity playlist;

    @ManyToOne
    @MapsId("songId")
    @JoinColumn(name = "SongId")
    private SongEntity song;

    @Column(name = "Position")
    private int position;

    public PlaylistSongsLinksEntity(final SongEntity song, final int position) {
        this.song = song;
        this.position = position;
    }
}
