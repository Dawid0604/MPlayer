package pl.dawid0604.mplayer.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {

    @Query("""
            SELECT new pl.dawid0604.mplayer.playlist.PlaylistEntity(p.encryptedId, p.name, p.createdDate)
            FROM #{#entityName} p
            WHERE p.user.id = :loggedUserId
            """)
    List<PlaylistEntity> findUserPlaylists(long loggedUserId);

    @Query("""
            SELECT new pl.dawid0604.mplayer.playlist.PlaylistEntity(p.encryptedId, p.name, p.createdDate)
            FROM #{#entityName} p
            WHERE p.id = :playlistId
           """)
    Optional<PlaylistEntity> findDetailsById(final long playlistId);

    @Query(value = """
                SELECT s.EncryptedId, s.Title, s.ThumbnailPath, s.SoundLink,
                       GROUP_CONCAT(DISTINCT sa.Name ORDER BY sa.Name ASC SEPARATOR ', ') AS Authors
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
}
