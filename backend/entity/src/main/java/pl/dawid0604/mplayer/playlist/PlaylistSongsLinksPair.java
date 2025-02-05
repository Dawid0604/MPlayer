package pl.dawid0604.mplayer.playlist;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class PlaylistSongsLinksPair implements Serializable {
    private long playlistId;
    private long songId;
}
