package pl.dawid0604.mplayer.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SongMoodRepository extends JpaRepository<SongMoodEntity, Long> {

    @Query("""
            SELECT new pl.dawid0604.mplayer.song.SongMoodEntity(m.encryptedId, m.name, m.color)
            FROM #{#entityName} m
            ORDER BY m.name
           """)
    List<SongMoodEntity> findAllMoods();

    @Modifying
    @Transactional
    @Query(value = """
                INSERT INTO SongMoodsLinks (SongId, MoodId)
                VALUES (:songId, :moodId)
            """, nativeQuery = true)
    void saveSongMoodPair(long moodId, long songId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SongMoodsLinks", nativeQuery = true)
    void deleteFromLinksTable();
}
