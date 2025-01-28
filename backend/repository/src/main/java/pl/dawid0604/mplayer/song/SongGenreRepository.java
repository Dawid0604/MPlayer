package pl.dawid0604.mplayer.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongGenreRepository extends JpaRepository<SongGenreEntity, Long> {

    @Query("""
            SELECT new pl.dawid0604.mplayer.song.SongGenreEntity(g.encryptedId, g.name, g.color)
            FROM #{#entityName} g
            ORDER BY g.name
           """)
    List<SongGenreEntity> findAllGenres();
}
