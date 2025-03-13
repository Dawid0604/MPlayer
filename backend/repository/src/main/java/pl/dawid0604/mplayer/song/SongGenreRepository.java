package pl.dawid0604.mplayer.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SongGenreRepository extends JpaRepository<SongGenreEntity, Long> {

    @Query("""
            SELECT new pl.dawid0604.mplayer.song.SongGenreEntity(g.encryptedId, g.name, g.color)
            FROM #{#entityName} g
            ORDER BY g.name
           """)
    List<SongGenreEntity> findAllGenres();

    @Modifying
    @Transactional
    @Query(value = """
                INSERT INTO SongGenresLinks (SongId, GenreId)
                VALUES (:songId, :genreId)
            """, nativeQuery = true)
    void saveSongGenrePair(long genreId, long songId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SongGenresLinks", nativeQuery = true)
    void deleteFromLinksTable();
}
