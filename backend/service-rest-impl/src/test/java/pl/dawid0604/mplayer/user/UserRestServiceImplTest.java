package pl.dawid0604.mplayer.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.dawid0604.mplayer.encryption.EncryptionService;
import pl.dawid0604.mplayer.exception.ResourceExistException;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;

import java.lang.reflect.Field;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.dawid0604.mplayer.tools.DateFormatter.getCurrentDate;

@ExtendWith(MockitoExtension.class)
class UserRestServiceImplTest {
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;
    @Mock private UserDaoService userDaoService;
    @Mock private UserRoleDaoService userRoleDaoService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EncryptionService encryptionService;
    @InjectMocks private UserRestServiceImpl userRestService;

    private static final String USERNAME;
    private static final String USER_ROLE;

    static {
        try {
            Field userRoleField = UserRestServiceImpl.class.getDeclaredField("USER_ROLE");
                  userRoleField.setAccessible(true);
                  USER_ROLE = (String) userRoleField.get(null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        USERNAME = "xyz";
    }

    @Test
    void shouldGetLoggedUserId() {
        // Given
        initContext();
        long userId = 1L;

        given(userDaoService.findIdByUsername(USERNAME))
                .willReturn(Optional.of(userId));

        // When
        var result = userRestService.getLoggedUserId();

        // Then
        assertEquals(userId, result);
    }

    @Test
    void shouldNotGetLoggedUserIdAndThrowResourceNotFoundExceptionWhenPrincipalIsNull() {
        // Given
        initContext();
        given(authentication.getPrincipal())
                .willReturn(null);

        // When
        // Then
        assertThat(catchThrowable(() -> userRestService.getLoggedUserId()))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(userDaoService);
    }

    @Test
    void shouldNotGetLoggedUserIdAndThrowResourceNotFoundExceptionWhenUserNotFound() {
        // Given
        initContext();

        // When
        // Then
        assertThat(catchThrowable(() -> userRestService.getLoggedUserId()))
                                                       .isInstanceOf(ResourceNotFoundException.class);

        verify(userDaoService).findIdByUsername(eq(USERNAME));
    }

    @Test
    void shouldRegister() {
        // Given
        ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        UserRoleEntity foundRole = UserRoleEntity.builder()
                                                 .id(5L)
                                                 .name("USER")
                                                 .build();

        String password = "xyz2";
        String nickname = "xyz3";
        String username = "xyz4";
        String encryptedId = "encryptedId";
        String encodedPassword = "encodedPassword";

        given(userRoleDaoService.findByName("USER"))
                .willReturn(Optional.of(foundRole));

        doAnswer(_inv -> setId(_inv.getArgument(0))).when(userDaoService)
                                                       .save(any(UserEntity.class));

        given(passwordEncoder.encode(eq(password)))
                .willReturn(encodedPassword);

        given(encryptionService.encryptUserId(any(Long.class)))
                .willReturn(encryptedId);

        // When
        userRestService.register(username, password, nickname);

        // Then
        verify(userDaoService, times(2)).save(argumentCaptor.capture());
        verify(userDaoService).existsByUsername(eq(username));
        verify(userDaoService).existsByNickname(eq(nickname));
        assertNull(argumentCaptor.getAllValues().get(0).getEncryptedId());
        assertEquals(encryptedId, argumentCaptor.getAllValues().get(1).getEncryptedId());
        assertEquals(encodedPassword, argumentCaptor.getAllValues().get(0).getPassword());
        assertEquals(getCurrentDate().truncatedTo(SECONDS), argumentCaptor.getAllValues().get(0).getCreatedDate().truncatedTo(SECONDS));
    }

    @Test
    void shouldNotRegisterWhenUsernameExists() {
        // Given
        String password = "xyz2";
        String nickname = "xyz3";
        String username = "xyz4";

        given(userDaoService.existsByUsername(eq(username)))
                .willThrow(ResourceExistException.userUsernameException(username));

        // When
        // Then
        assertThat(catchThrowable(() -> userRestService.register(username, password, nickname)))
                                                       .isInstanceOf(ResourceExistException.class);

        verifyNoInteractions(userRoleDaoService, passwordEncoder, encryptionService);
        verify(userDaoService, never()).save(any(UserEntity.class));
        verify(userDaoService, never()).existsByNickname(any());
    }

    @Test
    void shouldNotRegisterWhenNicknameExists() {
        // Given
        String password = "xyz2";
        String nickname = "xyz3";
        String username = "xyz4";

        given(userDaoService.existsByNickname(eq(nickname)))
                .willThrow(ResourceExistException.userNicknameException(nickname));

        // When
        // Then
        assertThat(catchThrowable(() -> userRestService.register(username, password, nickname)))
                                                       .isInstanceOf(ResourceExistException.class);

        verifyNoInteractions(userRoleDaoService, passwordEncoder, encryptionService);
        verify(userDaoService, never()).save(any(UserEntity.class));
        verify(userDaoService).existsByUsername(eq(username));
    }

    @Test
    void shouldNotRegisterWhenUserRoleNotExists() {
        // Given
        String password = "xyz2";
        String nickname = "xyz3";
        String username = "xyz4";

        given(userRoleDaoService.findByName(eq(USER_ROLE)))
                .willThrow(ResourceNotFoundException.userRoleNotFoundException(USER_ROLE));

        // When
        // Then
        assertThat(catchThrowable(() -> userRestService.register(username, password, nickname)))
                                                       .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(passwordEncoder, encryptionService);
        verify(userDaoService, never()).save(any(UserEntity.class));
        verify(userDaoService).existsByUsername(eq(username));
        verify(userDaoService).existsByNickname(eq(nickname));
    }

    @Test
    void shouldDelete() {
        // Given
        initContext();
        long userId = 1L;

        given(userDaoService.findIdByUsername(USERNAME))
                .willReturn(Optional.of(userId));

        // When
        userRestService.delete();

        // Then
        verify(userDaoService).deleteById(eq(userId));
    }

    @Test
    void shouldNotDeleteWhenUserDoesNotExists() {
        // Given
        initContext();
        given(authentication.getPrincipal())
                .willReturn(null);

        // When
        // Then
        assertThat(catchThrowable(() -> userRestService.delete()))
                                                       .isInstanceOf(ResourceNotFoundException.class)
                                                       .hasMessage("Logged user not found");
        verifyNoInteractions(userDaoService);
    }

    @Test
    void shouldGetLoggedUserData() {
        // Given
        initContext();
        long userId = 1L;
        var foundUser = UserEntity.builder()
                                  .username(USERNAME)
                                  .role(UserRoleEntity.builder().name(USER_ROLE).build())
                                  .nickname("xyz")
                                  .build();

        given(userDaoService.findIdByUsername(USERNAME))
                .willReturn(Optional.of(userId));

        given(userDaoService.findUsernameRoleNicknameById(eq(userId)))
                .willReturn(Optional.of(foundUser));

        // When
        var result = userRestService.getLoggedUserData();

        // Then
        assertEquals(foundUser.getUsername(), result.username());
        assertEquals(foundUser.getRole().getName(), result.role());
        assertEquals(foundUser.getNickname(), result.nickname());
    }

    @Test
    void shouldGetLoggedUserDataButWithoutResult() {
        // Given
        initContext();
        long userId = 1L;

        given(userDaoService.findIdByUsername(USERNAME))
                .willReturn(Optional.of(userId));

        // When
        // Then
        assertThat(catchThrowable(() -> userRestService.getLoggedUserData()))
                                                       .isInstanceOf(ResourceNotFoundException.class);

        verify(userDaoService).findUsernameRoleNicknameById(eq(userId));
    }

    @Test
    void shouldUpdatePassword() {
        // Given
        initContext();
        String newPassword = "newPassword";
        String newEncodedPassword = "newEncodedPassword";
        long userId = 1L;

        given(userDaoService.findIdByUsername(USERNAME))
                .willReturn(Optional.of(userId));

        given(passwordEncoder.encode(eq(newPassword)))
                .willReturn("newEncodedPassword");

        // When
        userRestService.updatePassword(new UserUpdatePasswordRequest(newPassword));

        // Then
        verify(userDaoService).updatePassword(eq(userId), eq(newEncodedPassword));
    }

    @Test
    void shouldNotUpdatePasswordWhenUserDoesNotExists() {
        // Given
        String newPassword = "newPassword";

        initContext();
        given(authentication.getPrincipal())
                .willReturn(null);

        // When
        // Then
        assertThat(catchThrowable(() -> userRestService.updatePassword(new UserUpdatePasswordRequest(newPassword))))
                                                       .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(userDaoService, encryptionService, passwordEncoder);
    }

    private static UserEntity setId(final UserEntity userEntity) {
        return UserEntity.builder()
                         .id(userEntity.getId() == null ? 1L : userEntity.getId())
                         .nickname(userEntity.getNickname())
                         .password(userEntity.getPassword())
                         .username(userEntity.getUsername())
                         .role(userEntity.getRole())
                         .createdDate(userEntity.getCreatedDate())
                         .encryptedId(userEntity.getEncryptedId())
                         .build();
    }

    public void initContext() {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(User.builder()
                                           .username(USERNAME)
                                           .password("{noop}xyz2")
                                           .build());
    }
}