package pl.dawid0604.mplayer.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongAuthorRepository extends JpaRepository<SongAuthorEntity, Long> { }
