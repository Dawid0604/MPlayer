package pl.dawid0604.mplayer.playlist;

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
import pl.dawid0604.mplayer.song.SongAuthorEntity;
import pl.dawid0604.mplayer.song.SongAuthorRepository;
import pl.dawid0604.mplayer.song.SongEntity;
import pl.dawid0604.mplayer.song.SongRepository;
import pl.dawid0604.mplayer.user.UserEntity;
import pl.dawid0604.mplayer.user.UserRepository;
import pl.dawid0604.mplayer.user.UserRoleEntity;
import pl.dawid0604.mplayer.user.UserRoleRepository;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static pl.dawid0604.mplayer.tools.DateFormatter.getCurrentDate;

@Testcontainers
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringBootTestContext.class)
public class PlaylistRepositoryTest {

    @Autowired
    private PlaylistRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongAuthorRepository authorRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private SongRepository songRepository;

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
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
        authorRepository.deleteAll();
        songRepository.deleteAll();
    }

    @Test
    void shouldFindUserPlaylists() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
                   user.setRole(userRole);

        user = userRepository.save(user);
        PlaylistEntity firstPlaylist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        PlaylistEntity secondPlaylist = new PlaylistEntity("id#2", "name#2", getCurrentDate().minusDays(1), 1);
        PlaylistEntity thirdPlaylist = new PlaylistEntity("id#3", "name#3", getCurrentDate().minusDays(2), 2);

        firstPlaylist.setUser(user);
        secondPlaylist.setUser(user);
        thirdPlaylist.setUser(user);

        repository.save(firstPlaylist);
        repository.save(secondPlaylist);
        repository.save(thirdPlaylist);

        // Then
        var playlists = repository.findUserPlaylists(user.getId());
        assertEquals(playlists.get(0).getName(), firstPlaylist.getName());
        assertEquals(playlists.get(1).getName(), secondPlaylist.getName());
        assertEquals(playlists.get(2).getName(), thirdPlaylist.getName());
    }

    @Test
    void shouldFindDetailsById() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
                   user.setRole(userRole);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
                       playlist.setUser(userRepository.save(user));
                       playlist = repository.save(playlist);

        // Then
        var details = repository.findDetailsById(playlist.getId());
        assertTrue(details.isPresent() && details.get().getEncryptedId().equals(playlist.getEncryptedId()));
    }

    @Test
    void shouldFindPlaylistSongs() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
                   user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");
        SongAuthorEntity thirdAuthor = new SongAuthorEntity("author#3");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);
        thirdAuthor = authorRepository.save(thirdAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
                       playlist.setUser(user);
                       playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        SongEntity secondSong = new SongEntity("id#2", "song#2", "thumbnailPath#2", "soundLink#2", List.of(secondAuthor));
        SongEntity thirdSong = new SongEntity("id#3", "song#3", "thumbnailPath#3", "soundLink#3", List.of(thirdAuthor));

        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        secondSong.setReleaseDate(getCurrentDate().toLocalDate());
        thirdSong.setReleaseDate(getCurrentDate().toLocalDate());

        firstSong = songRepository.save(firstSong);
        secondSong = songRepository.save(secondSong);
        thirdSong = songRepository.save(thirdSong);

        repository.savePlaylistSongPair(playlist.getId(), firstSong.getId());
        repository.savePlaylistSongPair(playlist.getId(), secondSong.getId());
        repository.savePlaylistSongPair(playlist.getId(), thirdSong.getId());
        var results = repository.findPlaylistSongs(playlist.getId());

        // Then
        assertEquals(3, results.size());
        assertPlaylistSong(results.get(0), firstSong, 0);
        assertPlaylistSong(results.get(1), secondSong, 1);
        assertPlaylistSong(results.get(2), thirdSong, 2);
    }

    @Test
    void shouldCountSongsByPlaylistId() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
                   user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");
        SongAuthorEntity thirdAuthor = new SongAuthorEntity("author#3");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);
        thirdAuthor = authorRepository.save(thirdAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        SongEntity secondSong = new SongEntity("id#2", "song#2", "thumbnailPath#2", "soundLink#2", List.of(secondAuthor));
        SongEntity thirdSong = new SongEntity("id#3", "song#3", "thumbnailPath#3", "soundLink#3", List.of(thirdAuthor));

        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        secondSong.setReleaseDate(getCurrentDate().toLocalDate());
        thirdSong.setReleaseDate(getCurrentDate().toLocalDate());

        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(firstSong).getId());
        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(secondSong).getId());
        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(thirdSong).getId());

        // Then
        assertEquals(3, repository.countSongsByPlaylistId(playlist.getId()));
    }

    @Test
    void shouldFindSongWithNeighbourSongs() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");
        SongAuthorEntity thirdAuthor = new SongAuthorEntity("author#3");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);
        thirdAuthor = authorRepository.save(thirdAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        SongEntity secondSong = new SongEntity("id#2", "song#2", "thumbnailPath#2", "soundLink#2", List.of(secondAuthor));
        SongEntity thirdSong = new SongEntity("id#3", "song#3", "thumbnailPath#3", "soundLink#3", List.of(thirdAuthor));

        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        secondSong.setReleaseDate(getCurrentDate().toLocalDate());
        thirdSong.setReleaseDate(getCurrentDate().toLocalDate());

        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(firstSong).getId());
        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(secondSong).getId());
        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(thirdSong).getId());

        var firstSongResult = repository.findSongWithNeighbourSongs(playlist.getId(), firstSong.getId());
        var secondSongResult = repository.findSongWithNeighbourSongs(playlist.getId(), secondSong.getId());
        var thirdSongResult = repository.findSongWithNeighbourSongs(playlist.getId(), thirdSong.getId());

        // Then
        assertEquals(2, firstSongResult.size());
        assertEquals(firstSong.getId(), firstSongResult.get(0)[0]);
        assertEquals(secondSong.getId(), firstSongResult.get(1)[0]);
        assertEquals(0, firstSongResult.get(0)[1]);
        assertEquals(1, firstSongResult.get(1)[1]);

        assertEquals(3, secondSongResult.size());
        assertEquals(firstSong.getId(), secondSongResult.get(0)[0]);
        assertEquals(secondSong.getId(), secondSongResult.get(1)[0]);
        assertEquals(thirdSong.getId(), secondSongResult.get(2)[0]);
        assertEquals(0, secondSongResult.get(0)[1]);
        assertEquals(1, secondSongResult.get(1)[1]);
        assertEquals(2, secondSongResult.get(2)[1]);

        assertEquals(2, thirdSongResult.size());
        assertEquals(secondSong.getId(), thirdSongResult.get(0)[0]);
        assertEquals(thirdSong.getId(), thirdSongResult.get(1)[0]);
        assertEquals(1, thirdSongResult.get(0)[1]);
        assertEquals(2, thirdSongResult.get(1)[1]);
    }

    @Test
    void shouldSwapSongsPosition() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
                   user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");
        SongAuthorEntity thirdAuthor = new SongAuthorEntity("author#3");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);
        thirdAuthor = authorRepository.save(thirdAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        SongEntity secondSong = new SongEntity("id#2", "song#2", "thumbnailPath#2", "soundLink#2", List.of(secondAuthor));
        SongEntity thirdSong = new SongEntity("id#3", "song#3", "thumbnailPath#3", "soundLink#3", List.of(thirdAuthor));

        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        secondSong.setReleaseDate(getCurrentDate().toLocalDate());
        thirdSong.setReleaseDate(getCurrentDate().toLocalDate());

        repository.savePlaylistSongPair(playlist.getId(), (firstSong = songRepository.save(firstSong)).getId());
        repository.savePlaylistSongPair(playlist.getId(), (secondSong = songRepository.save(secondSong)).getId());
        repository.savePlaylistSongPair(playlist.getId(), (thirdSong = songRepository.save(thirdSong)).getId());

        var firstSongResult = repository.findSongWithNeighbourSongs(playlist.getId(), firstSong.getId());
        repository.swapSongsPosition(playlist.getId(), List.of(List.of((Number) firstSongResult.get(0)[0], (Number) firstSongResult.get(1)[1]),
                                                               List.of((Number) firstSongResult.get(1)[0], (Number) firstSongResult.get(0)[1])));

        var swappedFirstSongPosition = repository.findSongPosition(playlist.getId(), firstSong.getId());
        var swappedSecondSongPosition = repository.findSongPosition(playlist.getId(), secondSong.getId());
        var swappedThirdSongPosition = repository.findSongPosition(playlist.getId(), thirdSong.getId());

        // Then
        assertTrue(swappedFirstSongPosition.isPresent() && swappedFirstSongPosition.get() == 1);
        assertTrue(swappedSecondSongPosition.isPresent() && swappedSecondSongPosition.get() == 0);
        assertTrue(swappedThirdSongPosition.isPresent() && swappedThirdSongPosition.get() == 2);
    }

    @Test
    void shouldDeleteSong() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");
        SongAuthorEntity thirdAuthor = new SongAuthorEntity("author#3");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);
        thirdAuthor = authorRepository.save(thirdAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        SongEntity secondSong = new SongEntity("id#2", "song#2", "thumbnailPath#2", "soundLink#2", List.of(secondAuthor));
        SongEntity thirdSong = new SongEntity("id#3", "song#3", "thumbnailPath#3", "soundLink#3", List.of(thirdAuthor));

        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        secondSong.setReleaseDate(getCurrentDate().toLocalDate());
        thirdSong.setReleaseDate(getCurrentDate().toLocalDate());

        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(firstSong).getId());
        repository.savePlaylistSongPair(playlist.getId(), (secondSong = songRepository.save(secondSong)).getId());
        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(thirdSong).getId());

        // Then
        assertTrue(repository.findSongPosition(playlist.getId(), secondSong.getId()).isPresent());
        repository.deleteSong(playlist.getId(), secondSong.getId());
        assertTrue(repository.findSongPosition(playlist.getId(), secondSong.getId()).isEmpty());
    }

    @Test
    void shouldFindSongPosition() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");
        SongAuthorEntity thirdAuthor = new SongAuthorEntity("author#3");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);
        thirdAuthor = authorRepository.save(thirdAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        SongEntity secondSong = new SongEntity("id#2", "song#2", "thumbnailPath#2", "soundLink#2", List.of(secondAuthor));
        SongEntity thirdSong = new SongEntity("id#3", "song#3", "thumbnailPath#3", "soundLink#3", List.of(thirdAuthor));

        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        secondSong.setReleaseDate(getCurrentDate().toLocalDate());
        thirdSong.setReleaseDate(getCurrentDate().toLocalDate());

        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(firstSong).getId());
        repository.savePlaylistSongPair(playlist.getId(), (secondSong = songRepository.save(secondSong)).getId());
        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(thirdSong).getId());

        // Then
        var position = repository.findSongPosition(playlist.getId(), secondSong.getId());
        assertTrue(position.isPresent() && position.get() == 1);
    }

    @Test
    void shouldCorrectSongsPosition() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");
        SongAuthorEntity thirdAuthor = new SongAuthorEntity("author#3");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);
        thirdAuthor = authorRepository.save(thirdAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        SongEntity secondSong = new SongEntity("id#2", "song#2", "thumbnailPath#2", "soundLink#2", List.of(secondAuthor));
        SongEntity thirdSong = new SongEntity("id#3", "song#3", "thumbnailPath#3", "soundLink#3", List.of(thirdAuthor));

        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        secondSong.setReleaseDate(getCurrentDate().toLocalDate());
        thirdSong.setReleaseDate(getCurrentDate().toLocalDate());

        repository.savePlaylistSongPair(playlist.getId(), (firstSong = songRepository.save(firstSong)).getId());
        repository.savePlaylistSongPair(playlist.getId(), (secondSong = songRepository.save(secondSong)).getId());
        repository.savePlaylistSongPair(playlist.getId(), (thirdSong = songRepository.save(thirdSong)).getId());

        // Then
        var firstSongPosition = repository.findSongPosition(playlist.getId(), firstSong.getId());
        var secondSongPosition = repository.findSongPosition(playlist.getId(), secondSong.getId());
        var thirdSongPosition = repository.findSongPosition(playlist.getId(), thirdSong.getId());

        assertTrue(firstSongPosition.isPresent() && firstSongPosition.get() == 0);
        assertTrue(secondSongPosition.isPresent() && secondSongPosition.get() == 1);
        assertTrue(thirdSongPosition.isPresent() && thirdSongPosition.get() == 2);
        repository.correctSongsPosition(playlist.getId(), 0);

        assertTrue((firstSongPosition = repository.findSongPosition(playlist.getId(), firstSong.getId())).isPresent() && firstSongPosition.get() == 0);
        assertTrue((secondSongPosition = repository.findSongPosition(playlist.getId(), secondSong.getId())).isPresent() && secondSongPosition.get() == 0);
        assertTrue((thirdSongPosition = repository.findSongPosition(playlist.getId(), thirdSong.getId())).isPresent() && thirdSongPosition.get() == 1);
    }

    @Test
    void shouldDeletePlaylist() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);

        user = userRepository.save(user);
        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        // Then
        assertTrue(repository.existsById(playlist.getId()));
        repository.deletePlaylist(playlist.getId());
        assertFalse(repository.existsById(playlist.getId()));
    }

    @Test
    void shouldFindPlaylistPosition() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);
        user = userRepository.save(user);

        PlaylistEntity firstPlaylist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        PlaylistEntity secondPlaylist = new PlaylistEntity("id#2", "name#2", getCurrentDate().minusDays(1), 1);

        firstPlaylist.setUser(user);
        secondPlaylist.setUser(user);

        firstPlaylist = repository.save(firstPlaylist);
        secondPlaylist = repository.save(secondPlaylist);

        // Then
        var firstPlaylistPosition = repository.findPlaylistPosition(firstPlaylist.getId());
        var secondPlaylistPosition = repository.findPlaylistPosition(secondPlaylist.getId());

        assertTrue(firstPlaylistPosition.isPresent() && firstPlaylistPosition.get() == 0);
        assertTrue(secondPlaylistPosition.isPresent() && secondPlaylistPosition.get() == 1);
    }

    @Test
    void shouldCorrectPlaylistsPosition() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);
        user = userRepository.save(user);

        PlaylistEntity firstPlaylist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        PlaylistEntity secondPlaylist = new PlaylistEntity("id#2", "name#2", getCurrentDate().minusDays(1), 1);
        PlaylistEntity thirdPlaylist = new PlaylistEntity("id#3", "name#3", getCurrentDate().minusDays(1), 2);

        firstPlaylist.setUser(user);
        secondPlaylist.setUser(user);
        thirdPlaylist.setUser(user);

        firstPlaylist = repository.save(firstPlaylist);
        secondPlaylist = repository.save(secondPlaylist);
        thirdPlaylist = repository.save(thirdPlaylist);

        // Then
        var firstPlaylistPosition = repository.findPlaylistPosition(firstPlaylist.getId());
        var secondPlaylistPosition = repository.findPlaylistPosition(secondPlaylist.getId());
        var thirdPlaylistPosition = repository.findPlaylistPosition(thirdPlaylist.getId());

        assertTrue(firstPlaylistPosition.isPresent() && firstPlaylistPosition.get() == 0);
        assertTrue(secondPlaylistPosition.isPresent() && secondPlaylistPosition.get() == 1);
        assertTrue(thirdPlaylistPosition.isPresent() && thirdPlaylistPosition.get() == 2);
        repository.correctPlaylistsPosition(user.getId(), 0);

        assertTrue((firstPlaylistPosition = repository.findPlaylistPosition(firstPlaylist.getId())).isPresent() && firstPlaylistPosition.get() == 0);
        assertTrue((secondPlaylistPosition = repository.findPlaylistPosition(secondPlaylist.getId())).isPresent() && secondPlaylistPosition.get() == 0);
        assertTrue((thirdPlaylistPosition = repository.findPlaylistPosition(thirdPlaylist.getId())).isPresent() && thirdPlaylistPosition.get() == 1);
    }

    @Test
    void shouldFindPlaylistWithNeighbourPlaylists() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);
        user = userRepository.save(user);

        PlaylistEntity firstPlaylist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        PlaylistEntity secondPlaylist = new PlaylistEntity("id#2", "name#2", getCurrentDate().minusDays(1), 1);
        PlaylistEntity thirdPlaylist = new PlaylistEntity("id#3", "name#3", getCurrentDate().minusDays(1), 2);

        firstPlaylist.setUser(user);
        secondPlaylist.setUser(user);
        thirdPlaylist.setUser(user);

        firstPlaylist = repository.save(firstPlaylist);
        secondPlaylist = repository.save(secondPlaylist);
        thirdPlaylist = repository.save(thirdPlaylist);

        // Then
        var firstPlaylistResult = repository.findPlaylistWithNeighbourPlaylists(firstPlaylist.getId());
        var secondPlaylistResult = repository.findPlaylistWithNeighbourPlaylists(secondPlaylist.getId());
        var thirdPlaylistResult = repository.findPlaylistWithNeighbourPlaylists(thirdPlaylist.getId());

        assertEquals(2, firstPlaylistResult.size());
        assertEquals(firstPlaylist.getId(), firstPlaylistResult.get(0)[0]);
        assertEquals(secondPlaylist.getId(), firstPlaylistResult.get(1)[0]);
        assertEquals(0, firstPlaylistResult.get(0)[1]);
        assertEquals(1, firstPlaylistResult.get(1)[1]);

        assertEquals(3, secondPlaylistResult.size());
        assertEquals(firstPlaylist.getId(), secondPlaylistResult.get(0)[0]);
        assertEquals(secondPlaylist.getId(), secondPlaylistResult.get(1)[0]);
        assertEquals(thirdPlaylist.getId(), secondPlaylistResult.get(2)[0]);
        assertEquals(0, secondPlaylistResult.get(0)[1]);
        assertEquals(1, secondPlaylistResult.get(1)[1]);
        assertEquals(2, secondPlaylistResult.get(2)[1]);

        assertEquals(2, thirdPlaylistResult.size());
        assertEquals(secondPlaylist.getId(), thirdPlaylistResult.get(0)[0]);
        assertEquals(thirdPlaylist.getId(), thirdPlaylistResult.get(1)[0]);
        assertEquals(1, thirdPlaylistResult.get(0)[1]);
        assertEquals(2, thirdPlaylistResult.get(1)[1]);
    }

    @Test
    void shouldSwapPlaylistsPosition() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);
        user = userRepository.save(user);

        PlaylistEntity firstPlaylist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        PlaylistEntity secondPlaylist = new PlaylistEntity("id#2", "name#2", getCurrentDate().minusDays(1), 1);

        firstPlaylist.setUser(user);
        secondPlaylist.setUser(user);

        firstPlaylist = repository.save(firstPlaylist);
        secondPlaylist = repository.save(secondPlaylist);

        var firstSongResult = repository.findPlaylistWithNeighbourPlaylists(firstPlaylist.getId());
        repository.swapPlaylistsPosition(List.of(List.of((Number) firstSongResult.get(0)[0], (Number) firstSongResult.get(1)[1]),
                                                 List.of((Number) firstSongResult.get(1)[0], (Number) firstSongResult.get(0)[1])));

        var swappedFirstPlaylistPosition = repository.findPlaylistPosition(firstPlaylist.getId());
        var swappedSecondPlaylistPosition = repository.findPlaylistPosition(secondPlaylist.getId());

        // Then
        assertTrue(swappedFirstPlaylistPosition.isPresent() && swappedFirstPlaylistPosition.get() == 1);
        assertTrue(swappedSecondPlaylistPosition.isPresent() && swappedSecondPlaylistPosition.get() == 0);
    }

    @Test
    void shouldRenamePlaylist() {
        // Given
        String newPlaylistName = "new playlist name";
        String oldPlaylistName = "name#1";

        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);
        user = userRepository.save(user);

        PlaylistEntity playlist = new PlaylistEntity("id#1", oldPlaylistName, getCurrentDate().minusDays(1), 0);
                       playlist.setUser(user);
                       playlist = repository.save(playlist);

        // Then
        repository.renamePlaylist(playlist.getId(), newPlaylistName);

        var v = repository.findById(playlist.getId());
        assertEquals(newPlaylistName, repository.findById(playlist.getId()).orElseThrow().getName());
    }

    @Test
    void playlistSongExistsById() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(firstSong).getId());

        // Then
        assertEquals(1, repository.playlistSongExistsById(playlist.getId(), firstSong.getId()).orElseThrow());
    }

    @Test
    void playlistNameExistsByUserId() {
        // Given
        String playlistName = "name#1";

        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);
        user = userRepository.save(user);

        PlaylistEntity playlist = new PlaylistEntity("id#1", playlistName, getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        repository.save(playlist);

        // Then
        assertTrue(repository.playlistNameExistsByUserId(user.getId(), playlistName));
    }

    @Test
    void shouldFindLastPlaylistPosition() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);
        user = userRepository.save(user);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        repository.save(playlist);

        // Then
        assertEquals(0, repository.findLastPlaylistPosition(user.getId()).orElseThrow());
    }

    @Test
    void shouldFindLastPlaylistSongPosition() {
        // Given
        // When
        UserRoleEntity userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        UserEntity user = new UserEntity();
        user.setRole(userRole);

        SongAuthorEntity firstAuthor = new SongAuthorEntity("author#1");
        SongAuthorEntity secondAuthor = new SongAuthorEntity("author#2");

        user = userRepository.save(user);
        firstAuthor = authorRepository.save(firstAuthor);
        secondAuthor = authorRepository.save(secondAuthor);

        PlaylistEntity playlist = new PlaylistEntity("id#1", "name#1", getCurrentDate().minusDays(1), 0);
        playlist.setUser(user);
        playlist = repository.save(playlist);

        SongEntity firstSong = new SongEntity("id#1", "song#1", "thumbnailPath#1", "soundLink#1", List.of(firstAuthor, secondAuthor));
        SongEntity secondSong = new SongEntity("id#2", "song#2", "thumbnailPath#2", "soundLink#2", List.of(secondAuthor));

        firstSong.setReleaseDate(getCurrentDate().toLocalDate());
        secondSong.setReleaseDate(getCurrentDate().toLocalDate());

        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(firstSong).getId());
        repository.savePlaylistSongPair(playlist.getId(), songRepository.save(secondSong).getId());

        // Then
        assertEquals(1, repository.findLastPlaylistSongPosition(playlist.getId()));
    }

    private static void assertPlaylistSong(final Object[] fields, final SongEntity song, final int expectedPosition) {
        assertEquals(song.getEncryptedId(), fields[0]);
        assertEquals(song.getTitle(), fields[1]);
        assertEquals(song.getThumbnailPath(), fields[2]);
        assertEquals(song.getSoundLink(), fields[3]);
        assertEquals(expectedPosition, fields[5]);
        assertEquals(song.getAuthors()
                         .stream()
                         .map(SongAuthorEntity::getName)
                         .collect(joining(", ")), fields[4]);
    }
}
