package pl.dawid0604.mplayer.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = { UserSpringBootTestApplicationContext.class }, webEnvironment = RANDOM_PORT)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRestService userRestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldDeleteAccount() throws Exception {
        // Given
        // When
        // Then
        mockMvc.perform(delete(getUrl()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldNotDeleteAccountWhenUserDoesNotExist() throws Exception {
        // Given
        doThrow(ResourceNotFoundException.userException()).when(userRestService).delete();

        // When
        // Then
        mockMvc.perform(delete(getUrl()))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.Message").exists());
    }

    @Test
    void shouldGetLoggedUserDate() throws Exception {
        // Given
        var response = new UserDataDTO("username", "nickname", "role");

        given(userRestService.getLoggedUserData())
                .willReturn(response);

        // When
        // Then
        mockMvc.perform(get(getUrl()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").exists())
               .andExpect(jsonPath("$.username").value(response.username()))
               .andExpect(jsonPath("$.nickname").value(response.nickname()))
               .andExpect(jsonPath("$.role").value(response.role()));
    }

    @Test
    void shouldNotGetLoggedUserDateThenUserDoesNotExist() throws Exception {
        // Given
        given(userRestService.getLoggedUserData())
                .willThrow(ResourceNotFoundException.userException());

        // When
        // Then
        mockMvc.perform(get(getUrl()))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.Message").exists());
    }

    @Test
    void shouldUpdatePassword() throws Exception {
        // Given
        var payload = new UserUpdatePasswordRequest("newPassword");

        // When
        // Then
        mockMvc.perform(patch(getUrl()).contentType(APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldNotUpdatePasswordAndReturnStatus400() throws Exception {
        // Given
        var payload = new UserUpdatePasswordRequest("xyz");

        // When
        // Then
        mockMvc.perform(patch(getUrl()).contentType(APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists());

        verify(userRestService, never()).updatePassword(any());
    }

    private static String getUrl() {
        return API_PATH + "user";
    }
}