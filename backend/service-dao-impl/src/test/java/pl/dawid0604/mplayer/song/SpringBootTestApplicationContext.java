package pl.dawid0604.mplayer.song;

import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {
        "pl.dawid0604.mplayer"
})
@EnableJpaRepositories(basePackages = {
        "pl.dawid0604.mplayer"
})
@ComponentScan(basePackages = {
        "pl.dawid0604.mplayer"
})
public class SpringBootTestApplicationContext {

    @Bean
    public SongDaoService songDaoService(final SongRepository songRepository, final EntityManager entityManager) {
        return new SongDaoServiceImpl(songRepository, entityManager);
    }
}
