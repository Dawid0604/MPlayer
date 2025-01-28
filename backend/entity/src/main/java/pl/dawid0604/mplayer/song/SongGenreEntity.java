package pl.dawid0604.mplayer.song;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import pl.dawid0604.mplayer.EntityBase;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SongGenres")
@EqualsAndHashCode(callSuper = true)
public class SongGenreEntity extends EntityBase {

    @Column(name = "Name")
    private String name;

    @Column(name = "Color")
    private String color;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "genres")
    private List<SongEntity> songs;

    public SongGenreEntity(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public SongGenreEntity(final String encryptedId, final String name,
                           final String color) {

        super(encryptedId);
        this.name = name;
        this.color = color;
    }
}
