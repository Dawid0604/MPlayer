package pl.dawid0604.mplayer.user;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.dawid0604.mplayer.SpringBootTestContext;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringBootTestContext.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserRoleRepository userRoleRepository;

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
        userRoleRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void shouldFindUsernamePasswordRoleByUsername() {
        // Given
        // When
        String username = "xyz";
        UserRoleEntity userRole = new UserRoleEntity("ROLE_USER");
        UserRoleEntity adminRole = new UserRoleEntity("ROLE_ADMIN");
        userRole =  userRoleRepository.save(userRole);
        adminRole = userRoleRepository.save(adminRole);

        UserEntity firstUser = new UserEntity("repository/user", "repository/user", userRole);
        UserEntity secondUser = new UserEntity("user2", "user2", userRole);
        UserEntity thirdUser = new UserEntity("admin", "admin", adminRole);
        UserEntity currentUser = new UserEntity(username, "xyz", userRole);

        repository.save(firstUser);
        repository.save(secondUser);
        repository.save(thirdUser);
        repository.save(currentUser);

        var result = repository.findUsernamePasswordRoleByUsername(username);

        // Then
        assertTrue(() -> {
            if(result.isEmpty()) {
                return false;
            }

            var _result = result.get();
            return Objects.equals(_result.getUsername(), username)                   &&
                   Objects.equals(_result.getPassword(), currentUser.getPassword())  &&
                   Objects.equals(_result.getRole().getName(), currentUser.getRole().getName());
        });
    }

    @Test
    void shouldFindUsernamePasswordRoleById() {
        // Given
        // When
        UserRoleEntity userRole = new UserRoleEntity("ROLE_USER");
        UserRoleEntity adminRole = new UserRoleEntity("ROLE_ADMIN");
        userRole =  userRoleRepository.save(userRole);
        adminRole = userRoleRepository.save(adminRole);

        UserEntity firstUser = new UserEntity("repository/user", userRole, "repository/user");
        UserEntity secondUser = new UserEntity("user2",  userRole, "user2");
        UserEntity thirdUser = new UserEntity("admin", adminRole, "admin");
        UserEntity currentUser = new UserEntity("user3", userRole, "user3");

        repository.save(firstUser);
        repository.save(secondUser);
        repository.save(thirdUser);
        var user = repository.save(currentUser);

        var result = repository.findUsernameRoleNicknameById(user.getId());

        // Then
        assertTrue(() -> {
            if(result.isEmpty()) {
                return false;
            }

            var _result = result.get();
            return Objects.equals(_result.getUsername(), currentUser.getUsername())  &&
                   Objects.equals(_result.getNickname(), currentUser.getNickname())  &&
                   Objects.equals(_result.getRole().getName(), currentUser.getRole().getName());
        });
    }

    @Test
    void shouldFindIdByUsername() {
        // Given
        // When
        String username = "xyz";
        UserRoleEntity userRole = new UserRoleEntity("ROLE_USER");
        UserRoleEntity adminRole = new UserRoleEntity("ROLE_ADMIN");
        userRole =  userRoleRepository.save(userRole);
        adminRole = userRoleRepository.save(adminRole);

        UserEntity firstUser = new UserEntity("repository/user", userRole, "repository/user");
        UserEntity secondUser = new UserEntity("user2",  userRole, "user2");
        UserEntity thirdUser = new UserEntity("admin", adminRole, "admin");
        UserEntity currentUser = new UserEntity(username, userRole, "user3");

        repository.save(firstUser);
        repository.save(secondUser);
        repository.save(thirdUser);
        repository.save(currentUser);

        var result = repository.findIdByUsername(username);

        // Then
        assertTrue(result.isPresent() && result.get().equals(currentUser.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"repository/user", "User", "usEr"})
    void shouldReturnTrueWhenUsernameExistsByUsernameIgnoreCase(final String username) {
        // Given
        // When
        var userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        repository.save(new UserEntity(username, userRole, "repository/user"));

        // Then
        assertTrue(repository.existsByUsernameIgnoreCase(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"repository/user", "User", "usEr"})
    void shouldReturnTrueWhenNicknameExistsByNicknameIgnoreCase(final String nickname) {
        // Given
        // When
        var userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        repository.save(new UserEntity("xyz", userRole, nickname));

        // Then
        assertTrue(repository.existsByNicknameIgnoreCase(nickname));
    }

    @Test
    void shouldDeleteByIdCustom() {
        // Given
        // When
        var userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        var user = repository.save(new UserEntity("xyz", userRole, "xyz"));

        assertTrue(repository.existsById(user.getId()));
        repository.deleteByIdCustom(user.getId());
        assertFalse(repository.existsById(user.getId()));
    }

    @Test
    void shouldUpdatePassword() {
        // Given
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String oldPassword = "xyz";
        String encodedOldPassword = passwordEncoder.encode(oldPassword);

        String newPassword = "zyx";
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        // When
        var userRole = userRoleRepository.save(new UserRoleEntity("ROLE_USER"));
        var user = repository.save(new UserEntity("xyz", encodedOldPassword, "xyz", userRole));
        repository.updatePassword(user.getId(), encodedNewPassword);

        // Then
        var possibleUser = repository.findUsernamePasswordRoleByUsername("xyz");
        assertTrue(possibleUser.isPresent() && passwordEncoder.matches(newPassword, possibleUser.get().getPassword()));
    }
}