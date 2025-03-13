package pl.dawid0604.mplayer.song;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.dawid0604.mplayer.EntityBase;

import java.util.List;

@Entity
@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SongMoods")
@EqualsAndHashCode(callSuper = true)
public class SongMoodEntity extends EntityBase {

    @Column(name = "Name")
    private String name;

    @Column(name = "Color")
    private String color;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "moods")
    private List<SongEntity> songs;

    public SongMoodEntity(final String encryptedId, final String name,
                          final String color) {

        super(encryptedId);
        this.name = name;
        this.color = color;
    }
}
