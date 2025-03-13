package pl.dawid0604.mplayer.song;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringBootTestApplicationContext.class)
class SongDaoServiceImplTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongAuthorRepository songAuthorRepository;

    @Autowired
    private SongMoodRepository songMoodRepository;

    @Autowired
    private SongGenreRepository songGenreRepository;

    @Autowired
    @Qualifier("songDaoService")
    private SongDaoService service;

    @SuppressWarnings("resource")
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.11.10-jammy")
                                                      .withDatabaseName("mplayer_db")
                                                      .withEnv("MYSQL_USER", "root")
                                                      .withEnv("MYSQL_ROOT_PASSWORD", "")
                                                      .withEnv("MYSQL_ALLOW_EMPTY_PASSWORD", "yes")
                                                      .withUsername("root")
                                                      .withPassword("")
            .withCommand("--ft_min_word_len=2")
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
        songRepository.deleteAll();
        songAuthorRepository.deleteAll();
        songMoodRepository.deleteAll();
        songGenreRepository.deleteAll();
    }

    @Test
    void shouldFindWelcomePopularSongs() {
        // Given
        SongAuthorEntity firstAuthor = SongAuthorEntity.builder()
                                                       .encryptedId("authorId#1")
                                                       .name("authorName#1")
                                                       .build();

        SongAuthorEntity secondAuthor = SongAuthorEntity.builder()
                                                        .encryptedId("authorId#2")
                                                        .name("authorName#2")
                                                        .build();

        SongAuthorEntity thirdAuthor = SongAuthorEntity.builder()
                                                       .encryptedId("authorId#3")
                                                       .name("authorName#3")
                                                       .build();

        SongMoodEntity firstMood = SongMoodEntity.builder()
                                                 .encryptedId("moodId#1")
                                                 .name("moodName#1")
                                                 .color("#123")
                                                 .build();

        SongMoodEntity secondMood = SongMoodEntity.builder()
                                                  .encryptedId("moodId#2")
                                                  .name("moodName#2")
                                                  .color("#234")
                                                  .build();

        SongGenreEntity firstGenre = SongGenreEntity.builder()
                                                    .encryptedId("genreId#1")
                                                    .name("genreName#1")
                                                    .color("#456")
                                                    .build();

        SongGenreEntity secondGenre = SongGenreEntity.builder()
                                                  .encryptedId("genreId#2")
                                                  .name("genreName#2")
                                                  .color("#789")
                                                  .build();

        SongEntity firstSong = SongEntity.builder()
                                         .encryptedId("songId#1")
                                         .releaseDate(LocalDate.of(2021, 11, 15))
                                         .numberOfListens(5)
                                         .soundLink("soundLink#1")
                                         .thumbnailPath("thumbnailPath#1")
                                         .title("songTitle#1")
                                         .build();

        SongEntity secondSong = SongEntity.builder()
                                          .encryptedId("songId#2")
                                          .releaseDate(LocalDate.of(2022, 1, 1))
                                          .numberOfListens(51)
                                          .soundLink("soundLink#2")
                                          .thumbnailPath("thumbnailPath#2")
                                          .title("songTitle#2")
                                          .build();

        SongEntity thirdSong = SongEntity.builder()
                                         .encryptedId("songId#3")
                                         .releaseDate(LocalDate.of(2024, 1, 1))
                                         .numberOfListens(6)
                                         .soundLink("soundLink#3")
                                         .thumbnailPath("thumbnailPath#3")
                                         .title("songTitle#3")
                                         .build();

        SongEntity forthSong = SongEntity.builder()
                                         .encryptedId("songId#4")
                                         .releaseDate(LocalDate.of(2024, 1, 1))
                                         .numberOfListens(4)
                                         .soundLink("soundLink#4")
                                         .thumbnailPath("thumbnailPath#4")
                                         .title("songTitle#4")
                                         .build();

        SongEntity fifthSong = SongEntity.builder()
                                         .encryptedId("songId#5")
                                         .releaseDate(LocalDate.of(2024, 12, 1))
                                         .numberOfListens(3)
                                         .soundLink("soundLink#5")
                                         .thumbnailPath("thumbnailPath#5")
                                         .title("songTitle#5")
                                         .build();

        SongEntity sixthSong = SongEntity.builder()
                                         .encryptedId("songId#6")
                                         .releaseDate(LocalDate.of(2023, 12, 1))
                                         .numberOfListens(2)
                                         .soundLink("soundLink#6")
                                         .thumbnailPath("thumbnailPath#6")
                                         .title("songTitle#6")
                                         .build();

        SongEntity seventhSong = SongEntity.builder()
                                           .encryptedId("songId#7")
                                           .releaseDate(LocalDate.of(2023, 12, 3))
                                           .numberOfListens(1)
                                           .soundLink("soundLink#7")
                                           .thumbnailPath("thumbnailPath#7")
                                           .title("songTitle#7")
                                           .build();
        
        firstAuthor = songAuthorRepository.save(firstAuthor);
        secondAuthor = songAuthorRepository.save(secondAuthor);
        thirdAuthor = songAuthorRepository.save(thirdAuthor);

        firstMood = songMoodRepository.save(firstMood);
        secondMood = songMoodRepository.save(secondMood);
        
        firstGenre = songGenreRepository.save(firstGenre);
        secondGenre = songGenreRepository.save(secondGenre);
        
        firstSong = songRepository.save(firstSong);
        secondSong = songRepository.save(secondSong);
        thirdSong = songRepository.save(thirdSong);
        forthSong = songRepository.save(forthSong);
        fifthSong = songRepository.save(fifthSong);
        sixthSong = songRepository.save(sixthSong);
        seventhSong = songRepository.save(seventhSong);

        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), firstSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), firstSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), secondSong.getId());
        songAuthorRepository.saveSongAuthorPair(thirdAuthor.getId(), thirdSong.getId());
        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), forthSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), fifthSong.getId());
        songAuthorRepository.saveSongAuthorPair(thirdAuthor.getId(), sixthSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), sixthSong.getId());
        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), sixthSong.getId());
        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), seventhSong.getId());

        songMoodRepository.saveSongMoodPair(firstMood.getId(), firstSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), firstSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), secondSong.getId());
        songMoodRepository.saveSongMoodPair(firstMood.getId(), thirdSong.getId());
        songMoodRepository.saveSongMoodPair(firstMood.getId(), forthSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), fifthSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), sixthSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), seventhSong.getId());

        songGenreRepository.saveSongGenrePair(firstGenre.getId(), firstSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), firstSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), secondSong.getId());
        songGenreRepository.saveSongGenrePair(firstGenre.getId(), thirdSong.getId());
        songGenreRepository.saveSongGenrePair(firstGenre.getId(), forthSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), fifthSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), sixthSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), seventhSong.getId());

        firstSong = songRepository.findByIdWithAuthors(firstSong.getId()).orElseThrow();
        secondSong = songRepository.findByIdWithAuthors(secondSong.getId()).orElseThrow();
        thirdSong = songRepository.findByIdWithAuthors(thirdSong.getId()).orElseThrow();
        forthSong = songRepository.findByIdWithAuthors(forthSong.getId()).orElseThrow();
        fifthSong = songRepository.findByIdWithAuthors(fifthSong.getId()).orElseThrow();
        sixthSong = songRepository.findByIdWithAuthors(sixthSong.getId()).orElseThrow();

        // When
        var result = service.findWelcomePopularSongs();

        // Then
        assertEquals(6, result.size());
        assertSong(secondSong, result.get(0));
        assertSong(thirdSong, result.get(1));
        assertSong(firstSong, result.get(2));
        assertSong(forthSong, result.get(3));
        assertSong(fifthSong, result.get(4));
        assertSong(sixthSong, result.get(5));
    }

    @Test
    void shouldFindWelcomeRecentSongReleases() {
        // Given
        SongAuthorEntity firstAuthor = SongAuthorEntity.builder()
                                                       .encryptedId("authorId#1")
                                                       .name("authorName#1")
                                                       .build();

        SongAuthorEntity secondAuthor = SongAuthorEntity.builder()
                                                        .encryptedId("authorId#2")
                                                        .name("authorName#2")
                                                        .build();

        SongAuthorEntity thirdAuthor = SongAuthorEntity.builder()
                                                       .encryptedId("authorId#3")
                                                       .name("authorName#3")
                                                       .build();

        SongMoodEntity firstMood = SongMoodEntity.builder()
                                                 .encryptedId("moodId#1")
                                                 .name("moodName#1")
                                                 .color("#123")
                                                 .build();

        SongMoodEntity secondMood = SongMoodEntity.builder()
                                                  .encryptedId("moodId#2")
                                                  .name("moodName#2")
                                                  .color("#234")
                                                  .build();

        SongGenreEntity firstGenre = SongGenreEntity.builder()
                                                    .encryptedId("genreId#1")
                                                    .name("genreName#1")
                                                    .color("#456")
                                                    .build();

        SongGenreEntity secondGenre = SongGenreEntity.builder()
                                                  .encryptedId("genreId#2")
                                                  .name("genreName#2")
                                                  .color("#789")
                                                  .build();

        SongEntity firstSong = SongEntity.builder()
                                         .encryptedId("songId#1")
                                         .releaseDate(LocalDate.of(2021, 11, 15))
                                         .numberOfListens(5)
                                         .soundLink("soundLink#1")
                                         .thumbnailPath("thumbnailPath#1")
                                         .title("songTitle#1")
                                         .build();

        SongEntity secondSong = SongEntity.builder()
                                          .encryptedId("songId#2")
                                          .releaseDate(LocalDate.of(2022, 1, 1))
                                          .numberOfListens(51)
                                          .soundLink("soundLink#2")
                                          .thumbnailPath("thumbnailPath#2")
                                          .title("songTitle#2")
                                          .build();

        SongEntity thirdSong = SongEntity.builder()
                                         .encryptedId("songId#3")
                                         .releaseDate(LocalDate.of(2024, 1, 1))
                                         .numberOfListens(6)
                                         .soundLink("soundLink#3")
                                         .thumbnailPath("thumbnailPath#3")
                                         .title("songTitle#3")
                                         .build();

        SongEntity forthSong = SongEntity.builder()
                                         .encryptedId("songId#4")
                                         .releaseDate(LocalDate.of(2024, 3, 1))
                                         .numberOfListens(4)
                                         .soundLink("soundLink#4")
                                         .thumbnailPath("thumbnailPath#4")
                                         .title("songTitle#4")
                                         .build();

        SongEntity fifthSong = SongEntity.builder()
                                         .encryptedId("songId#5")
                                         .releaseDate(LocalDate.of(2024, 12, 1))
                                         .numberOfListens(3)
                                         .soundLink("soundLink#5")
                                         .thumbnailPath("thumbnailPath#5")
                                         .title("songTitle#5")
                                         .build();

        SongEntity sixthSong = SongEntity.builder()
                                         .encryptedId("songId#6")
                                         .releaseDate(LocalDate.of(2023, 12, 1))
                                         .numberOfListens(2)
                                         .soundLink("soundLink#6")
                                         .thumbnailPath("thumbnailPath#6")
                                         .title("songTitle#6")
                                         .build();

        SongEntity seventhSong = SongEntity.builder()
                                           .encryptedId("songId#7")
                                           .releaseDate(LocalDate.of(2023, 12, 3))
                                           .numberOfListens(1)
                                           .soundLink("soundLink#7")
                                           .thumbnailPath("thumbnailPath#7")
                                           .title("songTitle#7")
                                           .build();

        firstAuthor = songAuthorRepository.save(firstAuthor);
        secondAuthor = songAuthorRepository.save(secondAuthor);
        thirdAuthor = songAuthorRepository.save(thirdAuthor);

        firstMood = songMoodRepository.save(firstMood);
        secondMood = songMoodRepository.save(secondMood);

        firstGenre = songGenreRepository.save(firstGenre);
        secondGenre = songGenreRepository.save(secondGenre);

        firstSong = songRepository.save(firstSong);
        secondSong = songRepository.save(secondSong);
        thirdSong = songRepository.save(thirdSong);
        forthSong = songRepository.save(forthSong);
        fifthSong = songRepository.save(fifthSong);
        sixthSong = songRepository.save(sixthSong);
        seventhSong = songRepository.save(seventhSong);

        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), firstSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), firstSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), secondSong.getId());
        songAuthorRepository.saveSongAuthorPair(thirdAuthor.getId(), thirdSong.getId());
        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), forthSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), fifthSong.getId());
        songAuthorRepository.saveSongAuthorPair(thirdAuthor.getId(), sixthSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), sixthSong.getId());
        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), sixthSong.getId());
        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), seventhSong.getId());

        songMoodRepository.saveSongMoodPair(firstMood.getId(), firstSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), firstSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), secondSong.getId());
        songMoodRepository.saveSongMoodPair(firstMood.getId(), thirdSong.getId());
        songMoodRepository.saveSongMoodPair(firstMood.getId(), forthSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), fifthSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), sixthSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), seventhSong.getId());

        songGenreRepository.saveSongGenrePair(firstGenre.getId(), firstSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), firstSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), secondSong.getId());
        songGenreRepository.saveSongGenrePair(firstGenre.getId(), thirdSong.getId());
        songGenreRepository.saveSongGenrePair(firstGenre.getId(), forthSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), fifthSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), sixthSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), seventhSong.getId());

        firstSong = songRepository.findByIdWithAuthors(firstSong.getId()).orElseThrow();
        secondSong = songRepository.findByIdWithAuthors(secondSong.getId()).orElseThrow();
        thirdSong = songRepository.findByIdWithAuthors(thirdSong.getId()).orElseThrow();
        forthSong = songRepository.findByIdWithAuthors(forthSong.getId()).orElseThrow();
        fifthSong = songRepository.findByIdWithAuthors(fifthSong.getId()).orElseThrow();
        sixthSong = songRepository.findByIdWithAuthors(sixthSong.getId()).orElseThrow();
        seventhSong = songRepository.findByIdWithAuthors(seventhSong.getId()).orElseThrow();

        // When
        var result = service.findWelcomeRecentSongReleases();

        // Then
        assertEquals(7, result.size());
        assertSong(fifthSong, result.get(0));
        assertSong(forthSong, result.get(1));
        assertSong(thirdSong, result.get(2));
        assertSong(seventhSong, result.get(3));
        assertSong(sixthSong, result.get(4));
        assertSong(secondSong, result.get(5));
        assertSong(firstSong, result.get(6));
    }

    @Test
    void shouldHandleSongListening() {
        // Given
        SongAuthorEntity firstAuthor = SongAuthorEntity.builder()
                .encryptedId("authorId#1")
                .name("authorName#1")
                .build();

        SongMoodEntity firstMood = SongMoodEntity.builder()
                .encryptedId("moodId#1")
                .name("moodName#1")
                .color("#123")
                .build();

        SongGenreEntity firstGenre = SongGenreEntity.builder()
                .encryptedId("genreId#1")
                .name("genreName#1")
                .color("#456")
                .build();

        SongEntity firstSong = SongEntity.builder()
                .encryptedId("songId#1")
                .releaseDate(LocalDate.of(2021, 11, 15))
                .numberOfListens(5)
                .soundLink("soundLink#1")
                .thumbnailPath("thumbnailPath#1")
                .title("songTitle#1")
                .build();

        firstAuthor = songAuthorRepository.save(firstAuthor);
        firstMood = songMoodRepository.save(firstMood);
        firstGenre = songGenreRepository.save(firstGenre);
        firstSong = songRepository.save(firstSong);

        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), firstSong.getId());
        songMoodRepository.saveSongMoodPair(firstMood.getId(), firstSong.getId());
        songGenreRepository.saveSongGenrePair(firstGenre.getId(), firstSong.getId());

        // When
        service.handleSongListening(firstSong.getId());

        // Then
        assertEquals(firstSong.getNumberOfListens() + 1, songRepository.findById(firstSong.getId())
                                                                                .orElseThrow()
                                                                                .getNumberOfListens());
    }

    @Test
    void shouldExistsById() {
        // Given
        SongAuthorEntity firstAuthor = SongAuthorEntity.builder()
                .encryptedId("authorId#1")
                .name("authorName#1")
                .build();

        SongMoodEntity firstMood = SongMoodEntity.builder()
                .encryptedId("moodId#1")
                .name("moodName#1")
                .color("#123")
                .build();

        SongGenreEntity firstGenre = SongGenreEntity.builder()
                .encryptedId("genreId#1")
                .name("genreName#1")
                .color("#456")
                .build();

        SongEntity firstSong = SongEntity.builder()
                .encryptedId("songId#1")
                .releaseDate(LocalDate.of(2021, 11, 15))
                .numberOfListens(5)
                .soundLink("soundLink#1")
                .thumbnailPath("thumbnailPath#1")
                .title("songTitle#1")
                .build();

        firstAuthor = songAuthorRepository.save(firstAuthor);
        firstMood = songMoodRepository.save(firstMood);
        firstGenre = songGenreRepository.save(firstGenre);
        firstSong = songRepository.save(firstSong);

        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), firstSong.getId());
        songMoodRepository.saveSongMoodPair(firstMood.getId(), firstSong.getId());
        songGenreRepository.saveSongGenrePair(firstGenre.getId(), firstSong.getId());

        // When
        // Then
        assertTrue(service.existsById(firstSong.getId()));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void shouldDiscoverWithSelectedGenres(final int page) {
        // Given
        var discoverSongs = getDiscoverSongs();
        SongEntity firstSong = discoverSongs.songs().get(0);
        SongEntity secondSong = discoverSongs.songs().get(1);
        SongEntity thirdSong = discoverSongs.songs().get(2);

        // When
        var result = service.discover(null, List.of(discoverSongs.genres().get(1).getEncryptedId()), null, page, 2);

        // Then
        switch (page) {
            case 0 -> {
                assertEquals(2, result.getNumberOfElements());
                assertEquals(0, result.getNumber());
                assertEquals(3, result.getTotalElements());
                assertSong(firstSong, result.getContent().get(0));
                assertSong(secondSong, result.getContent().get(1));
            }

            case 1 -> {
                assertEquals(1, result.getNumber());
                assertEquals(1, result.getNumberOfElements());
                assertEquals(3, result.getTotalElements());
                assertSong(thirdSong, result.getContent().get(0));
            }

            case 2 -> {
                assertEquals(3, result.getTotalElements());
                assertTrue(result.isEmpty());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void shouldDiscoverWithSelectedAllGenres(final int page) {
        // Given
        var discoverSongs = getDiscoverSongs();
        SongEntity firstSong = discoverSongs.songs().get(0);
        SongEntity thirdSong = discoverSongs.songs().get(2);

        // When
        var result = service.discover(null, List.of(discoverSongs.genres().get(0).getEncryptedId(),
                                                                discoverSongs.genres().get(1).getEncryptedId()), null, page, 2);

        // Then
        switch (page) {
            case 0 -> {
                assertEquals(2, result.getNumberOfElements());
                assertEquals(0, result.getNumber());
                assertEquals(2, result.getTotalElements());
                assertSong(firstSong, result.getContent().get(0));
                assertSong(thirdSong, result.getContent().get(1));
            }

            case 1 -> {
                assertEquals(2, result.getTotalElements());
                assertTrue(result.isEmpty());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void shouldDiscoverWithSelectedMoods(final int page) {
        // Given
        var discoverSongs = getDiscoverSongs();
        SongEntity firstSong = discoverSongs.songs().get(0);
        SongEntity secondSong = discoverSongs.songs().get(1);
        SongEntity thirdSong = discoverSongs.songs().get(2);

        // When
        var result = service.discover(null, null, List.of(discoverSongs.moods().get(1).getEncryptedId()), page, 2);

        // Then
        switch (page) {
            case 0 -> {
                assertEquals(2, result.getNumberOfElements());
                assertEquals(0, result.getNumber());
                assertEquals(3, result.getTotalElements());
                assertSong(firstSong, result.getContent().get(0));
                assertSong(secondSong, result.getContent().get(1));
            }

            case 1 -> {
                assertEquals(1, result.getNumber());
                assertEquals(1, result.getNumberOfElements());
                assertEquals(3, result.getTotalElements());
                assertSong(thirdSong, result.getContent().get(0));
            }

            case 2 -> {
                assertEquals(3, result.getTotalElements());
                assertTrue(result.isEmpty());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void shouldDiscoverWithSelectedAllMoods(final int page) {
        // Given
        var discoverSongs = getDiscoverSongs();
        SongEntity firstSong = discoverSongs.songs().get(0);
        SongEntity thirdSong = discoverSongs.songs().get(2);

        // When
        var result = service.discover(null, null, List.of(discoverSongs.moods().get(0).getEncryptedId(),
                                                                            discoverSongs.moods().get(1).getEncryptedId()), page, 2);

        // Then
        switch (page) {
            case 0 -> {
                assertEquals(2, result.getNumberOfElements());
                assertEquals(0, result.getNumber());
                assertEquals(2, result.getTotalElements());
                assertSong(firstSong, result.getContent().get(0));
                assertSong(thirdSong, result.getContent().get(1));
            }

            case 1 -> {
                assertEquals(2, result.getTotalElements());
                assertTrue(result.isEmpty());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void shouldDiscoverBySimpleSearchedText(final int page) {
        // Given
        SongAuthorEntity author = new SongAuthorEntity();
        author.setName("any author name");
        author.setEncryptedId("authorId#1");

        SongAuthorEntity secondAuthor = new SongAuthorEntity();
        secondAuthor.setName("any author2");
        secondAuthor.setEncryptedId("authorId#2");

        SongMoodEntity mood = new SongMoodEntity();
        mood.setEncryptedId("moodId#1");
        mood.setName("moodName#1");
        mood.setColor("#1234");

        SongGenreEntity genre = new SongGenreEntity();
        genre.setEncryptedId("genreId#1");
        genre.setName("genreName#1");
        genre.setColor("#2345");

        SongEntity song = new SongEntity();
        song.setReleaseDate(LocalDate.now());
        song.setEncryptedId("songId#1");
        song.setTitle("any song title");
        song.setThumbnailPath("songThumbnailPath#1");
        song.setSoundLink("songSoundLink#1");

        SongEntity secondSong = new SongEntity();
        secondSong.setReleaseDate(LocalDate.now());
        secondSong.setEncryptedId("songId#2");
        secondSong.setTitle("any song title2 name");
        secondSong.setThumbnailPath("songThumbnailPath#2");
        secondSong.setSoundLink("songSoundLink#2");

        song = songRepository.save(song);
        secondSong = songRepository.save(secondSong);
        mood = songMoodRepository.save(mood);
        genre = songGenreRepository.save(genre);
        author = songAuthorRepository.save(author);
        secondAuthor = songAuthorRepository.save(secondAuthor);

        songMoodRepository.saveSongMoodPair(mood.getId(), song.getId());
        songGenreRepository.saveSongGenrePair(genre.getId(), song.getId());
        songAuthorRepository.saveSongAuthorPair(author.getId(), song.getId());

        songMoodRepository.saveSongMoodPair(mood.getId(), secondSong.getId());
        songGenreRepository.saveSongGenrePair(genre.getId(), secondSong.getId());
        songAuthorRepository.saveSongAuthorPair(author.getId(), secondSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), secondSong.getId());

        song = songRepository.findByIdWithAuthors(song.getId()).orElseThrow();
        secondSong = songRepository.findByIdWithAuthors(secondSong.getId()).orElseThrow();

        // When
        var result = service.discover("name", null, null, page, 2);

        // Then
        switch (page) {
            case 0 -> {
                assertEquals(2, result.getNumberOfElements());
                assertEquals(0, result.getNumber());
                assertEquals(2, result.getTotalElements());
                assertSong(song, result.getContent().get(0));
                assertSong(secondSong, result.getContent().get(1));
            }

            case 1 -> {
                assertEquals(2, result.getTotalElements());
                assertTrue(result.isEmpty());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void shouldDiscoverByComplexSearchedText(final int page) {
        // Given
        SongAuthorEntity author = new SongAuthorEntity();
        author.setName("any author name");
        author.setEncryptedId("authorId#1");

        SongAuthorEntity secondAuthor = new SongAuthorEntity();
        secondAuthor.setName("any author2");
        secondAuthor.setEncryptedId("authorId#2");

        SongMoodEntity mood = new SongMoodEntity();
        mood.setEncryptedId("moodId#1");
        mood.setName("moodName#1");
        mood.setColor("#1234");

        SongGenreEntity genre = new SongGenreEntity();
        genre.setEncryptedId("genreId#1");
        genre.setName("genreName#1");
        genre.setColor("#2345");

        SongEntity song = new SongEntity();
        song.setReleaseDate(LocalDate.now());
        song.setEncryptedId("songId#1");
        song.setTitle("any song title");
        song.setThumbnailPath("songThumbnailPath#1");
        song.setSoundLink("songSoundLink#1");

        SongEntity secondSong = new SongEntity();
        secondSong.setReleaseDate(LocalDate.now());
        secondSong.setEncryptedId("songId#2");
        secondSong.setTitle("any song title2 name");
        secondSong.setThumbnailPath("songThumbnailPath#2");
        secondSong.setSoundLink("songSoundLink#2");

        song = songRepository.save(song);
        secondSong = songRepository.save(secondSong);
        mood = songMoodRepository.save(mood);
        genre = songGenreRepository.save(genre);
        author = songAuthorRepository.save(author);
        secondAuthor = songAuthorRepository.save(secondAuthor);

        songMoodRepository.saveSongMoodPair(mood.getId(), song.getId());
        songGenreRepository.saveSongGenrePair(genre.getId(), song.getId());
        songAuthorRepository.saveSongAuthorPair(author.getId(), song.getId());

        songMoodRepository.saveSongMoodPair(mood.getId(), secondSong.getId());
        songGenreRepository.saveSongGenrePair(genre.getId(), secondSong.getId());
        songAuthorRepository.saveSongAuthorPair(author.getId(), secondSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), secondSong.getId());

        song = songRepository.findByIdWithAuthors(song.getId()).orElseThrow();
        secondSong = songRepository.findByIdWithAuthors(secondSong.getId()).orElseThrow();

        // When
        var result = service.discover("any name", null, null, page, 2);

        // Then
        switch (page) {
            case 0 -> {
                assertEquals(2, result.getNumberOfElements());
                assertEquals(0, result.getNumber());
                assertEquals(2, result.getTotalElements());
                assertSong(song, result.getContent().get(0));
                assertSong(secondSong, result.getContent().get(1));
            }

            case 1 -> {
                assertEquals(2, result.getTotalElements());
                assertTrue(result.isEmpty());
            }
        }
    }

    private static void assertSong(final SongEntity expectedSong, final SongEntity actualSong) {
        String expectedSongAuthors = expectedSong.getAuthors()
                                                 .stream()
                                                 .map(SongAuthorEntity::getName)
                                                 .collect(Collectors.joining(","));

        String actualSongAuthors = actualSong.getAuthors()
                                             .stream()
                                             .map(SongAuthorEntity::getName)
                                             .collect(Collectors.joining(","));

        assertEquals(expectedSong.getEncryptedId(), actualSong.getEncryptedId());
        assertEquals(expectedSong.getTitle(), actualSong.getTitle());
        assertEquals(expectedSong.getThumbnailPath(), actualSong.getThumbnailPath());
        assertEquals(expectedSong.getSoundLink(), actualSong.getSoundLink());
        assertEquals(expectedSongAuthors, actualSongAuthors);
    }

    private DiscoverSongs getDiscoverSongs() {
        SongAuthorEntity firstAuthor = SongAuthorEntity.builder()
                .encryptedId("authorId#1")
                .name("authorName#1")
                .build();

        SongAuthorEntity secondAuthor = SongAuthorEntity.builder()
                .encryptedId("authorId#2")
                .name("authorName#2")
                .build();

        SongAuthorEntity thirdAuthor = SongAuthorEntity.builder()
                .encryptedId("authorId#3")
                .name("authorName#3")
                .build();

        SongMoodEntity firstMood = SongMoodEntity.builder()
                .encryptedId("moodId#1")
                .name("moodName#1")
                .color("#123")
                .build();

        SongMoodEntity secondMood = SongMoodEntity.builder()
                .encryptedId("moodId#2")
                .name("moodName#2")
                .color("#234")
                .build();

        SongMoodEntity thirdMood = SongMoodEntity.builder()
                                                 .encryptedId("moodId#3")
                                                 .name("moodName#3")
                                                 .color("#254")
                                                 .build();

        SongGenreEntity firstGenre = SongGenreEntity.builder()
                .encryptedId("genreId#1")
                .name("genreName#1")
                .color("#456")
                .build();

        SongGenreEntity secondGenre = SongGenreEntity.builder()
                .encryptedId("genreId#2")
                .name("genreName#2")
                .color("#789")
                .build();

        SongGenreEntity thirdGenre = SongGenreEntity.builder()
                .encryptedId("genreId#3")
                .name("genreName#3")
                .color("#219")
                .build();

        SongEntity firstSong = SongEntity.builder()
                .encryptedId("songId#1")
                .releaseDate(LocalDate.of(2021, 11, 15))
                .numberOfListens(5)
                .soundLink("soundLink#1")
                .thumbnailPath("thumbnailPath#1")
                .title("songTitle#1")
                .build();

        SongEntity secondSong = SongEntity.builder()
                .encryptedId("songId#2")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .numberOfListens(51)
                .soundLink("soundLink#2")
                .thumbnailPath("thumbnailPath#2")
                .title("songTitle#2")
                .build();

        SongEntity thirdSong = SongEntity.builder()
                .encryptedId("songId#3")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .numberOfListens(6)
                .soundLink("soundLink#3")
                .thumbnailPath("thumbnailPath#3")
                .title("songTitle#3")
                .build();

        SongEntity forthSong = SongEntity.builder()
                .encryptedId("songId#4")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .numberOfListens(4)
                .soundLink("soundLink#4")
                .thumbnailPath("thumbnailPath#4")
                .title("songTitle#4")
                .build();

        firstAuthor = songAuthorRepository.save(firstAuthor);
        secondAuthor = songAuthorRepository.save(secondAuthor);
        thirdAuthor = songAuthorRepository.save(thirdAuthor);

        firstMood = songMoodRepository.save(firstMood);
        secondMood = songMoodRepository.save(secondMood);
        thirdMood = songMoodRepository.save(thirdMood);

        firstGenre = songGenreRepository.save(firstGenre);
        secondGenre = songGenreRepository.save(secondGenre);
        thirdGenre = songGenreRepository.save(thirdGenre);

        firstSong = songRepository.save(firstSong);
        secondSong = songRepository.save(secondSong);
        thirdSong = songRepository.save(thirdSong);
        forthSong = songRepository.save(forthSong);

        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), firstSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), firstSong.getId());
        songAuthorRepository.saveSongAuthorPair(secondAuthor.getId(), secondSong.getId());
        songAuthorRepository.saveSongAuthorPair(thirdAuthor.getId(), thirdSong.getId());
        songAuthorRepository.saveSongAuthorPair(firstAuthor.getId(), forthSong.getId());

        songGenreRepository.saveSongGenrePair(firstGenre.getId(), firstSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), firstSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), secondSong.getId());
        songGenreRepository.saveSongGenrePair(firstGenre.getId(), thirdSong.getId());
        songGenreRepository.saveSongGenrePair(secondGenre.getId(), thirdSong.getId());
        songGenreRepository.saveSongGenrePair(thirdGenre.getId(), forthSong.getId());
        
        songMoodRepository.saveSongMoodPair(firstMood.getId(), firstSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), firstSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), secondSong.getId());
        songMoodRepository.saveSongMoodPair(firstMood.getId(), thirdSong.getId());
        songMoodRepository.saveSongMoodPair(secondMood.getId(), thirdSong.getId());
        songMoodRepository.saveSongMoodPair(thirdMood.getId(), forthSong.getId());

        firstSong = songRepository.findByIdWithAuthors(firstSong.getId()).orElseThrow();
        secondSong = songRepository.findByIdWithAuthors(secondSong.getId()).orElseThrow();
        thirdSong = songRepository.findByIdWithAuthors(thirdSong.getId()).orElseThrow();
        forthSong = songRepository.findByIdWithAuthors(forthSong.getId()).orElseThrow();

        return new DiscoverSongs(
                List.of(firstGenre, secondGenre, thirdGenre),
                List.of(firstMood, secondMood, thirdMood),
                List.of(firstSong, secondSong, thirdSong, forthSong),
                List.of(firstAuthor, secondAuthor, thirdAuthor));
    }

    private record DiscoverSongs(List<SongGenreEntity> genres, List<SongMoodEntity> moods,
                                 List<SongEntity> songs, List<SongAuthorEntity> authors) { }

}