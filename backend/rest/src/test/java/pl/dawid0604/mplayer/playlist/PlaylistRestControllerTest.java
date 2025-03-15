package pl.dawid0604.mplayer.playlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;
import pl.dawid0604.mplayer.playlist.request.PlaylistCreateRequestDTO;
import pl.dawid0604.mplayer.playlist.request.PlaylistRenameRequestDTO;
import pl.dawid0604.mplayer.song.SongDTO;

import java.util.List;

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
@SpringBootTest(classes = PlaylistSpringBootTestApplicationContext.class, webEnvironment = RANDOM_PORT)
class PlaylistRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaylistRestService playlistRestService;

    @Test
    void shouldFindUserPlaylists() throws Exception {
        // Given
        var result = List.of(new PlaylistDTO(), new PlaylistDTO());

        given(playlistRestService.findUserPlaylists())
                .willReturn(result);

        // When
        // Then
        mockMvc.perform(get(getUrl()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").exists())
               .andExpect(jsonPath("$", hasSize(result.size())));

        verify(playlistRestService, never()).findPlaylists(any());
    }

    @Test
    void shouldFindUserPlaylistsWithSongId() throws Exception {
        // Given
        String songId = "xyz";
        var result = List.of(new PlaylistWithSongDTO(), new PlaylistWithSongDTO());

        given(playlistRestService.findPlaylists(songId))
                .willReturn(result);

        // When
        // Then
        mockMvc.perform(get(getUrl()).param("song", songId))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").exists())
               .andExpect(jsonPath("$", hasSize(result.size())));

        verify(playlistRestService, never()).findUserPlaylists();
    }

    @Test
    void shouldFindPlaylistDetails() throws Exception {
        // Given
        String playlistId = "xyz";
        var playlist = new PlaylistDTO("xyz2", "xyz3", "2025-01-02", 5);
        var songs = List.of(new SongDTO(), new SongDTO());
        var response = new PlaylistDetailsDTO(playlist, songs);

        given(playlistRestService.findPlaylistDetails(playlistId))
                .willReturn(response);

        // When
        // Then
        mockMvc.perform(get(getUrl(playlistId)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").exists())
               .andExpect(jsonPath("$.playlist").exists())
               .andExpect(jsonPath("$.songs").exists())
               .andExpect(jsonPath("$.songs", hasSize(songs.size())))
               .andExpect(jsonPath("$.playlist.encryptedId").value(playlist.encryptedId()))
               .andExpect(jsonPath("$.playlist.name").value(playlist.name()))
               .andExpect(jsonPath("$.playlist.createdDate").value(playlist.createdDate()))
               .andExpect(jsonPath("$.playlist.numberOfSongs").value(playlist.numberOfSongs()));

        verify(playlistRestService, never()).findUserPlaylists();
    }

    @Test
    void shouldNotFindPlaylistDetailsAndReturnStatus400() throws Exception {
        // Given
        String playlistId = "xyz";
        given(playlistRestService.findPlaylistDetails(playlistId))
                .willThrow(ResourceNotFoundException.playlistNotFoundException(1));

        // When
        // Then
        mockMvc.perform(get(getUrl(playlistId)))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$").exists())
               .andExpect(jsonPath("$.Message").exists());

        verify(playlistRestService, never()).findUserPlaylists();
    }

    @Test
    void shouldIncreaseSongPosition() throws Exception {
        // Given
        String playlistId = "xyz1";
        String songId = "xyz2";

        // When
        // Then
        mockMvc.perform(patch(getUrl(playlistId + "/" + songId + "/increase")))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).increaseSongPosition(eq(playlistId), eq(songId));
    }

    @Test
    void shouldDecreaseSongPosition() throws Exception {
        // Given
        String playlistId = "xyz1";
        String songId = "xyz2";

        // When
        // Then
        mockMvc.perform(patch(getUrl(playlistId + "/" + songId + "/decrease")))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).decreaseSongPosition(eq(playlistId), eq(songId));
    }
    
    @Test
    void shouldDeleteSong() throws Exception {
        // Given
        String playlistId = "xyz1";
        String songId = "xyz2";
        
        // When
        // Then
        mockMvc.perform(delete(getUrl(playlistId + "/" + songId)))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).deleteSong(eq(playlistId), eq(songId));
    }

    @Test
    void shouldIncreasePlaylistPosition() throws Exception {
        // Given
        String playlistId = "xyz1";

        // When
        // Then
        mockMvc.perform(patch(getUrl(playlistId + "/increase")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).increasePlaylistPosition(eq(playlistId));
    }

    @Test
    void shouldDecreasePlaylistPosition() throws Exception {
        // Given
        String playlistId = "xyz1";

        // When
        // Then
        mockMvc.perform(patch(getUrl(playlistId + "/decrease")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).decreasePlaylistPosition(eq(playlistId));
    }


    @Test
    void shouldDeletePlaylist() throws Exception {
        // Given
        String playlistId = "xyz1";

        // When
        // Then
        mockMvc.perform(delete(getUrl(playlistId)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).deletePlaylist(eq(playlistId));
    }

    @Test
    void shouldRenamePlaylist() throws Exception {
        // Given
        var payload = new PlaylistRenameRequestDTO("xyz", "newName");

        // When
        // Then
        mockMvc.perform(patch(getUrl("rename")).contentType(APPLICATION_JSON)
                                                       .content(objectMapper.writeValueAsString(payload)))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).renamePlaylist(eq(payload.encryptedId()), eq(payload.name()));
    }

    @Test
    void shouldNotRenamePlaylistAndReturnStatus400() throws Exception {
        // Given
        var payload = new PlaylistRenameRequestDTO("xyz", "");

        // When
        // Then
        mockMvc.perform(patch(getUrl("rename")).contentType(APPLICATION_JSON)
                                                       .content(objectMapper.writeValueAsString(payload)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$").exists());

        verify(playlistRestService, never()).renamePlaylist(any(), any());
    }

    @Test
    void shouldCreatePlaylist() throws Exception{
        // Given
        var payload = new PlaylistCreateRequestDTO("newName");

        // When
        // Then
        mockMvc.perform(post(getUrl("create")).contentType(APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(payload)))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).createPlaylist(eq(payload.name()));
    }

    @Test
    void shouldNotCreatePlaylistAndReturnStatus400() throws Exception{
        // Given
        var payload = new PlaylistCreateRequestDTO("");

        // When
        // Then
        mockMvc.perform(post(getUrl("create")).contentType(APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(payload)))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$").exists());

        verify(playlistRestService, never()).createPlaylist(eq(payload.name()));
    }

    @Test
    void shouldAddSongToPlaylist() throws Exception {
        // Given
        String playlistId = "xyz1";
        String songId = "xyz2";

        // When
        // Then
        mockMvc.perform(post(getUrl(playlistId + "/" + songId + "/add")))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$").doesNotExist());

        verify(playlistRestService).addSongToPlaylist(eq(playlistId), eq(songId));
    }

    private static String getUrl(final String endpoint) {
        return API_PATH + "playlist/" + endpoint;
    }

    private static String getUrl() {
        return API_PATH + "playlist";
    }
}