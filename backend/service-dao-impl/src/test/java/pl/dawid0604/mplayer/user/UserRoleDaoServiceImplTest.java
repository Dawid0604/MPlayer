package pl.dawid0604.mplayer.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserRoleDaoServiceImplTest {
    @Mock private UserRoleRepository userRoleRepository;
    @InjectMocks private UserRoleDaoServiceImpl userRoleDaoService;

    @Test
    void shouldFindByName() {
        // Given
        String role = "ROLE_USER";

        // When
        // Then
        userRoleDaoService.findByName(role);
        verify(userRoleRepository).findByName(eq(role));
    }
}