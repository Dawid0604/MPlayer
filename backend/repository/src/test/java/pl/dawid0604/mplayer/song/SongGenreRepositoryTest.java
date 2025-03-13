package pl.dawid0604.mplayer.song;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.dawid0604.mplayer.SpringBootTestContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringBootTestContext.class)
class SongGenreRepositoryTest {

    @Autowired
    private SongGenreRepository repository;

    @SuppressWarnings("resource")
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:latest")
                                                      .withDatabaseName("mplayer_db")
                                                      .withEnv("MYSQL_USER", "root")
                                                      .withEnv("MYSQL_ROOT_PASSWORD", "")
                                                      .withEnv("MYSQL_ALLOW_EMPTY_PASSWORD", "yes")
                                                      .withUsername("root")
                                                      .withPassword("")
                                                      .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(MariaDBContainer.class)));

    @BeforeAll
    static void beforeAll() {
        mariaDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mariaDBContainer.stop();
    }

    @DynamicPropertySource
    @SuppressWarnings("unused")
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
        registry.add("spring.flyway.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.flyway.user", mariaDBContainer::getUsername);
        registry.add("spring.flyway.password", mariaDBContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldFindAllGenres() {
        // Given
        SongGenreEntity firstGenre = new SongGenreEntity("xyz#1", "C Genre#1", "Color#1");
        SongGenreEntity secondGenre = new SongGenreEntity("xyz#2", "A Genre#2", "Color#2");
        SongGenreEntity thirdGenre = new SongGenreEntity("xyz#3", "B Genre#3", "Color#3");

        // When
        repository.save(firstGenre);
        repository.save(secondGenre);
        repository.save(thirdGenre);

        // Then
        var genres = repository.findAllGenres();
        assertEquals(genres.get(0).getName(), secondGenre.getName());
        assertEquals(genres.get(1).getName(), thirdGenre.getName());
        assertEquals(genres.get(2).getName(), firstGenre.getName());
    }
}