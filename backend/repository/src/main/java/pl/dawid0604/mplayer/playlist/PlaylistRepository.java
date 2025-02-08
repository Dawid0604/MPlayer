package pl.dawid0604.mplayer.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {

    @Query("""
            SELECT new pl.dawid0604.mplayer.playlist.PlaylistEntity(p.encryptedId, p.name, p.createdDate, p.position)
            FROM #{#entityName} p
            WHERE p.user.id = :loggedUserId
            """)
    List<PlaylistEntity> findUserPlaylists(long loggedUserId);

    @Query("""
            SELECT new pl.dawid0604.mplayer.playlist.PlaylistEntity(p.encryptedId, p.name, p.createdDate, p.position)
            FROM #{#entityName} p
            WHERE p.id = :playlistId
           """)
    Optional<PlaylistEntity> findDetailsById(final long playlistId);

    @Query(value = """
                SELECT s.EncryptedId, s.Title, s.ThumbnailPath, s.SoundLink,
                       GROUP_CONCAT(DISTINCT sa.Name ORDER BY sa.Name ASC SEPARATOR ', ') AS Authors,
                       pls.position
                FROM Playlists as p
                INNER JOIN PlaylistsSongsLinks as pls ON pls.PlaylistId = p.Id
                INNER JOIN Songs as s ON pls.SongId = s.Id
                INNER JOIN SongAuthorsLinks as asl ON asl.SongId = s.Id
                INNER JOIN SongAuthors as sa ON asl.AuthorId = sa.Id
                WHERE p.Id = :playlistId
                GROUP BY s.Id
            """, nativeQuery = true)
    List<Object[]> findPlaylistSongs(long playlistId);

    @Query(value = """
                SELECT COUNT(s.Id)
                FROM PlaylistsSongsLinks pls
                INNER JOIN Songs s ON pls.SongId = s.Id
                WHERE pls.PlaylistId = :playlistId
            """, nativeQuery = true)
    long countSongsByPlaylistId(long playlistId);

    @Query(value = """
                SELECT pls.SongId, pls.Position
                FROM PlaylistsSongsLinks pls
                WHERE pls.PlaylistId = :playlistId AND
                      pls.Position IN (
                        SELECT sub_pls.Position - 1 FROM PlaylistsSongsLinks sub_pls WHERE sub_pls.SongId = :songId
                        UNION
                        SELECT sub_pls.Position FROM PlaylistsSongsLinks sub_pls WHERE sub_pls.SongId = :songId
                        UNION
                        SELECT sub_pls.Position + 1 FROM PlaylistsSongsLinks sub_pls WHERE sub_pls.SongId = :songId
                      ) ORDER BY pls.Position
           """, nativeQuery = true)
    List<Object[]> findSongWithNeighbourSongs(long playlistId, long songId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE PlaylistsSongsLinks as psl
            SET psl.position = CASE
                WHEN psl.SongId = :#{#ids[0][0]} THEN :#{#ids[0][1]}
                WHEN psl.SongId = :#{#ids[1][0]} THEN :#{#ids[1][1]}
            END
            WHERE psl.PlaylistId = :playlistId AND
                  psl.SongId IN (:#{#ids[0][0]}, :#{#ids[1][0]})
           """, nativeQuery = true)
    void swapSongsPosition(long playlistId, List<List<Number>> ids);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM PlaylistsSongsLinks
            WHERE PlaylistId = :playlistId AND SongId = :songId
           """, nativeQuery = true)
    void deleteSong(long playlistId, long songId);

    @Query(value = """
                SELECT pls.Position
                FROM PlaylistsSongsLinks pls
                WHERE pls.PlaylistId = :playlistId AND pls.SongId = :songId
            """, nativeQuery = true)
    Optional<Integer> findSongPosition(long playlistId, long songId);

    @Query(value = """
                UPDATE PlaylistsSongsLinks as psl
                SET psl.Position = psl.Position - 1
                WHERE psl.PlaylistId = :playlistId AND
                      psl.Position > :position
            """, nativeQuery = true)
    void correctSongsPosition(long playlistId, int position);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM Playlists
            WHERE Id = :playlistId
           """, nativeQuery = true)
    void deletePlaylist(long playlistId);

    @Query(value = """
                SELECT pl.Position
                FROM Playlists pl
                WHERE pl.Id = :playlistId
            """, nativeQuery = true)
    Optional<Integer> findPlaylistPosition(long playlistId);

    @Query(value = """
                UPDATE Playlists as pl
                SET psl.Position = pl.Position - 1
                WHERE pl.Id = :playlistId AND
                      pl.Position > :position
            """, nativeQuery = true)
    void correctPlaylistsPosition(long playlistId, int position);


    @Query(value = """
                SELECT pl.Id, pl.Position
                FROM Playlists pl
                WHERE pl.Position IN (
                        SELECT sub_pl.Position - 1 FROM Playlists sub_pl WHERE sub_pl.Id = :playlistId
                        UNION
                        SELECT sub_pl.Position FROM Playlists sub_pl WHERE sub_pl.Id = :playlistId
                        UNION
                        SELECT sub_pl.Position + 1 FROM Playlists sub_pl WHERE sub_pl.Id = :playlistId
                      ) ORDER BY pl.Position
           """, nativeQuery = true)
    List<Object[]> findPlaylistWithNeighbourPlaylists(long playlistId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE Playlists as ps
            SET ps.position = CASE
                WHEN ps.Id = :#{#ids[0][0]} THEN :#{#ids[0][1]}
                WHEN ps.Id = :#{#ids[1][0]} THEN :#{#ids[1][1]}
            END
            WHERE ps.Id IN (:#{#ids[0][0]}, :#{#ids[1][0]})
           """, nativeQuery = true)
    void swapPlaylistsPosition(List<List<Number>> ids);
}
