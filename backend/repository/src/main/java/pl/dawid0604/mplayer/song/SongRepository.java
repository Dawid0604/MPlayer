package pl.dawid0604.mplayer.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} s SET s.numberOfListens = s.numberOfListens + 1 WHERE s.id = :songId")
    void incrementNumberOfListening(long songId);
}
