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
@Table(name = "SongAuthors")
@EqualsAndHashCode(callSuper = true)
public class SongAuthorEntity extends EntityBase {

    @Column(name = "Name")
    private String name;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "authors")
    private List<SongEntity> songs;

    public SongAuthorEntity(final String name) {
        this.name = name;
    }
}
