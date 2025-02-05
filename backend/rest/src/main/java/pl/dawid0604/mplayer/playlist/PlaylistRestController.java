package pl.dawid0604.mplayer.playlist;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + "playlist")
public class PlaylistRestController {
    private final PlaylistRestService playlistRestService;

    @GetMapping
    @ResponseStatus(OK)
    public List<PlaylistDTO> findUserPlaylists() {
        return playlistRestService.findUserPlaylists();
    }

    @ResponseStatus(OK)
    @GetMapping("/{playlistId}")
    public PlaylistDetailsDTO findPlaylistDetails(@PathVariable("playlistId") final String playlistId) {
        return playlistRestService.findPlaylistDetails(playlistId);
    }
}
