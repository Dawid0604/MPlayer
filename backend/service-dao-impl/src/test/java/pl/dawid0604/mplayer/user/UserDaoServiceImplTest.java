package pl.dawid0604.mplayer.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDaoServiceImplTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private UserDaoServiceImpl userDaoService;

    @Test
    void shouldFindByUsername() {
        // Given
        String username = "username";

        // When
        // Then
        userDaoService.findByUsername(username);
        verify(userRepository).findUsernamePasswordRoleByUsername(eq(username));
    }

    @Test
    void shouldFindIdByUsername() {
        // Given
        String username = "username";

        // When
        // Then
        userDaoService.findIdByUsername(username);
        verify(userRepository).findIdByUsername(eq(username));
    }

    @Test
    void shouldExistsByUsername() {
        // Given
        String username = "username";

        // When
        // Then
        userDaoService.existsByUsername(username);
        verify(userRepository).existsByUsernameIgnoreCase(eq(username));
    }

    @Test
    void shouldExistsByNickname() {
        // Given
        String nickname = "nickname";

        // When
        // Then
        userDaoService.existsByNickname(nickname);
        verify(userRepository).existsByNicknameIgnoreCase(eq(nickname));
    }

    @Test
    void shouldSave() {
        // Given
        UserEntity entity = new UserEntity();

        // When
        // Then
        userDaoService.save(entity);
        verify(userRepository).save(eq(entity));
    }

    @Test
    void shouldDeleteById() {
        // Given
        long loggedUserId = 1;

        // When
        // Then
        userDaoService.deleteById(loggedUserId);
        verify(userRepository).deleteByIdCustom(eq(loggedUserId));
    }

    @Test
    void shouldFindUsernameRoleNicknameById() {
        // Given
        long loggedUserId = 1;

        // When
        // Then
        userDaoService.findUsernameRoleNicknameById(loggedUserId);
        verify(userRepository).findUsernameRoleNicknameById(eq(loggedUserId));
    }

    @Test
    void shouldUpdatePassword() {
        // Given
        long loggedUserId = 1;
        String password = "password";

        // When
        // Then
        userDaoService.updatePassword(loggedUserId, password);
        verify(userRepository).updatePassword(eq(loggedUserId), eq(password));
    }
}