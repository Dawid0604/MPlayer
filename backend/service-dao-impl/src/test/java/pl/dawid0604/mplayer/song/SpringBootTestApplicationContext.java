package pl.dawid0604.mplayer.song;

import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = {
        "pl.dawid0604.mplayer.playlist",
        "pl.dawid0604.mplayer.song",
        "pl.dawid0604.mplayer.user"
})
@EnableJpaRepositories(basePackages = {
        "pl.dawid0604.mplayer.playlist",
        "pl.dawid0604.mplayer.song",
        "pl.dawid0604.mplayer.user"
})
@ComponentScan(basePackages = {
        "pl.dawid0604.mplayer.playlist",
        "pl.dawid0604.mplayer.song",
        "pl.dawid0604.mplayer.user"
})
class SpringBootTestApplicationContext {

    @Bean
    public SongDaoService songDaoService(final SongRepository songRepository, final EntityManager entityManager) {
        return new SongDaoServiceImpl(songRepository, entityManager);
    }
}
