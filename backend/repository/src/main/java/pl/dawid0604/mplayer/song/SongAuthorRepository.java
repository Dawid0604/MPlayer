package pl.dawid0604.mplayer.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SongAuthorRepository extends JpaRepository<SongAuthorEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
                INSERT INTO SongAuthorsLinks (SongId, AuthorId)
                VALUES (:songId, :authorId)
            """, nativeQuery = true)
    void saveSongAuthorPair(long authorId, long songId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SongAuthorsLinks", nativeQuery = true)
    void deleteFromLinksTable();
}
