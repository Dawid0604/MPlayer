package pl.dawid0604.mplayer.song;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = { SongSpringBootTestApplicationContext.class }, webEnvironment = RANDOM_PORT)
class SongRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SongRestService songRestService;

    @MockBean
    private SongGenreRestService songGenreRestService;

    @MockBean
    private SongMoodRestService songMoodRestService;

    @Test
    void shouldFindWelcomeSongs() throws Exception {
        // Given
        var popularSongs = List.of(new SongDTO(), new SongDTO());
        var recentSongs = List.of(new SongDTO(), new SongDTO());

        given(songRestService.findWelcomeSongs())
                .willReturn(new WelcomeSongsDTO(popularSongs, recentSongs));

        // When
        // Then
        mockMvc.perform(get(getUrl("find/welcome")))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.popular").exists())
               .andExpect(jsonPath("$.recentReleases").exists())
               .andExpect(jsonPath("$.popular", hasSize(2)))
               .andExpect(jsonPath("$.recentReleases", hasSize(2)));
    }

    @Test
    void shouldDiscover() throws Exception {
        // Given
        var payload = new DiscoverPayload("searchedText", emptyList(), emptyList(), 1, 2);
        var discoveredSongs = List.of(new DiscoverSongsDTO.SongDTO(), new DiscoverSongsDTO.SongDTO());
        var response = new DiscoverSongsDTO(new DiscoverSongsDTO.Pageable(), discoveredSongs);

        given(songRestService.discover(payload))
                .willReturn(response);

        // When
        // Then
        mockMvc.perform(post(getUrl("discover")).contentType(APPLICATION_JSON)
                                                        .content(objectMapper.writeValueAsString(payload)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").exists())
               .andExpect(jsonPath("$.songs", hasSize(2)));
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "x;1;2",
            "x;-1;2",
            "x;1;0",
            "searchedTest;-1;2",
            "searchedTest;1;0"
    })
    void shouldNotDiscoverAndReturnStatus400(final String searchedText, final int pageNumber, final int pageSize) throws Exception {
        // Given
        var payload = new DiscoverPayload(searchedText, emptyList(), emptyList(), pageNumber, pageSize);

        // When
        // Then
        mockMvc.perform(post(getUrl("discover")).contentType(APPLICATION_JSON)
                                                        .content(objectMapper.writeValueAsString(payload)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$").exists());

        verify(songRestService, never()).discover(any());
    }

    @Test
    void shouldHandleSongListening() throws Exception {
        // Given
        String encryptedId = "xyz";

        // When
        // Then
        mockMvc.perform(patch(getUrl("/listening/" + encryptedId + "/handle")))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").doesNotExist());

        verify(songRestService).handleSongListening(eq(encryptedId));
    }

    @Test
    void shouldFindGenres() throws Exception {
        // Given
        var result = List.of(new SongGenreDTO(), new SongGenreDTO());

        given(songGenreRestService.findAll())
                .willReturn(result);

        // When
        // Then
        mockMvc.perform(get(getUrl("genres")))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").exists())
               .andExpect(jsonPath("$", hasSize(result.size())));
    }

    @Test
    void shouldFindMoods() throws Exception {
        // Given
        var result = List.of(new SongMoodDTO(), new SongMoodDTO());

        given(songMoodRestService.findAll())
                .willReturn(result);

        // When
        // Then
        mockMvc.perform(get(getUrl("moods")))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").exists())
               .andExpect(jsonPath("$", hasSize(result.size())));
    }

    private static String getUrl(final String endpoint) {
        return API_PATH + "song/" + endpoint;
    }
}