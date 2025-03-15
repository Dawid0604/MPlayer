package pl.dawid0604.mplayer.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.dawid0604.mplayer.exception.ResourceExistException;
import pl.dawid0604.mplayer.user.UserRegistrationRequest;
import pl.dawid0604.mplayer.user.UserRestService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = { AuthorizationSpringBootTestApplicationContext.class }, webEnvironment = RANDOM_PORT)
class AuthorizationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRestService userRestService;

    @Test
    void shouldRegisterAndReturnStatus200() throws Exception {
        // Given
        String username = "username";
        String nickname = "nickname";
        String password = "password";
        String content = objectMapper.writeValueAsString(new UserRegistrationRequest(username, nickname, password));

        // When
        // Then
        mockMvc.perform(post(getUrl()).contentType(APPLICATION_JSON)
                                      .content(content))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").doesNotExist());

        verify(userRestService).register(eq(username), eq(password), eq(nickname));
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            ";password;nickname",
            "username;;nickname",
            "username;password;",
            ";;",
    })
    void shouldNotRegisterAndReturnStatus400(final String username, final String password,
                                             final String nickname) throws Exception {
        // Given
        String content = objectMapper.writeValueAsString(new UserRegistrationRequest(username, nickname, password));

        // When
        // Then
        mockMvc.perform(post(getUrl()).contentType(APPLICATION_JSON)
                                      .content(content))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$").exists());

        verify(userRestService, never()).register(eq(username), eq(password), eq(nickname));
    }

    @Test
    void shouldNotRegisterWhenUserAlreadyExists() throws Exception {
        // Given
        String username = "username";
        String nickname = "nickname";
        String password = "password";
        String content = objectMapper.writeValueAsString(new UserRegistrationRequest(username, nickname, password));

        doThrow(ResourceExistException.userNicknameException(nickname)).when(userRestService)
                                                                       .register(eq(username), eq(password), eq(nickname));

        // When
        // Then
        mockMvc.perform(post(getUrl()).contentType(APPLICATION_JSON)
                                      .content(content))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").exists());

        verify(userRestService).register(eq(username), eq(password), eq(nickname));
    }

    private static String getUrl() {
        return API_PATH + "auth/register";
    }
}