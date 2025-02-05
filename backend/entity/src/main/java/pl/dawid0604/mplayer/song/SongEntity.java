package pl.dawid0604.mplayer.song;

import jakarta.persistence.*;
import lombok.*;
import pl.dawid0604.mplayer.EntityBase;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Songs")
@EqualsAndHashCode(callSuper = true)
public class SongEntity extends EntityBase {

    @Column(name = "Title")
    private String title;

    @Column(name = "SoundLink")
    private String soundLink;

    @Column(name = "ThumbnailPath")
    private String thumbnailPath;

    @Column(name = "NumberOfListens")
    private int numberOfListens;

    @EqualsAndHashCode.Exclude
    @Column(name = "ReleaseDate")
    private LocalDate releaseDate;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @JoinTable(name = "SongAuthorsLinks",
               joinColumns = @JoinColumn(name = "SongId"),
               inverseJoinColumns = @JoinColumn(name = "AuthorId"))
    private List<SongAuthorEntity> authors;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @JoinTable(name = "SongGenresLinks",
               joinColumns = @JoinColumn(name = "SongId"),
               inverseJoinColumns = @JoinColumn(name = "GenreId"))
    private List<SongGenreEntity> genres;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @JoinTable(name = "SongMoodsLinks",
               joinColumns = @JoinColumn(name = "SongId"),
               inverseJoinColumns = @JoinColumn(name = "MoodId"))
    private List<SongMoodEntity> moods;

    public SongEntity(final String encryptedId, final String title,
                      final String thumbnailPath, final String soundLink,
                      final List<SongAuthorEntity> authors) {

        super(encryptedId);
        this.title = title;
        this.thumbnailPath = thumbnailPath;
        this.authors = authors;
        this.soundLink = soundLink;
    }

    public SongEntity(final String encryptedId, final String title,
                      final String thumbnailPath, final String soundLink,
                      final LocalDate releaseDate, final List<SongAuthorEntity> authors,
                      final List<SongMoodEntity> moods, final List<SongGenreEntity> genres) {

        super(encryptedId);
        this.title = title;
        this.thumbnailPath = thumbnailPath;
        this.soundLink = soundLink;
        this.releaseDate = releaseDate;
        this.authors = authors;
        this.moods = moods;
        this.genres = genres;
    }
}
