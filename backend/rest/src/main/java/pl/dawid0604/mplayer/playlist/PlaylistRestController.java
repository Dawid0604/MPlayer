package pl.dawid0604.mplayer.playlist;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.dawid0604.mplayer.playlist.request.PlaylistCreateRequestDTO;
import pl.dawid0604.mplayer.playlist.request.PlaylistRenameRequestDTO;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.*;
import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + "playlist")
public class PlaylistRestController {
    private final PlaylistRestService playlistRestService;

    @GetMapping
    @ResponseStatus(OK)
    public List<?> findUserPlaylists(@RequestParam(name = "song", required = false) final String songId) {
        return (isBlank(songId)) ? playlistRestService.findUserPlaylists()
                                 : playlistRestService.findPlaylists(songId);
    }

    @ResponseStatus(OK)
    @GetMapping("/{playlistId}")
    public PlaylistDetailsDTO findPlaylistDetails(@PathVariable("playlistId") final String playlistId) {
        return playlistRestService.findPlaylistDetails(playlistId);
    }

    @ResponseStatus(OK)
    @PatchMapping("/{playlistId}/{songId}/increase")
    public void increaseSongPosition(@PathVariable("playlistId") final String playlistId,
                                     @PathVariable("songId") final String songId) {

        playlistRestService.increaseSongPosition(playlistId, songId);
    }

    @ResponseStatus(OK)
    @PatchMapping("/{playlistId}/{songId}/decrease")
    public void decreaseSongPosition(@PathVariable("playlistId") final String playlistId,
                                     @PathVariable("songId") final String songId) {

        playlistRestService.decreaseSongPosition(playlistId, songId);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{playlistId}/{songId}")
    public void deleteSong(@PathVariable("playlistId") final String playlistId,
                           @PathVariable("songId") final String songId) {

        playlistRestService.deleteSong(playlistId, songId);
    }

    @ResponseStatus(OK)
    @PatchMapping("/{playlistId}/increase")
    public void increasePlaylistPosition(@PathVariable("playlistId") final String playlistId) {
        playlistRestService.increasePlaylistPosition(playlistId);
    }

    @ResponseStatus(OK)
    @PatchMapping("/{playlistId}/decrease")
    public void decreasePlaylistPosition(@PathVariable("playlistId") final String playlistId) {
        playlistRestService.decreasePlaylistPosition(playlistId);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{playlistId}")
    public void deletePlaylist(@PathVariable("playlistId") final String playlistId) {
        playlistRestService.deletePlaylist(playlistId);
    }

    @PatchMapping("/rename")
    @ResponseStatus(NO_CONTENT)
    public void renamePlaylist(@RequestBody final PlaylistRenameRequestDTO request) {
        playlistRestService.renamePlaylist(request.encryptedId(), request.name());
    }

    @PostMapping("/create")
    @ResponseStatus(CREATED)
    public void createPlaylist(@RequestBody final PlaylistCreateRequestDTO request) {
        playlistRestService.createPlaylist(request.name());
    }

    @ResponseStatus(OK)
    @PostMapping("/{playlistId}/{songId}/add")
    public void addSongToPlaylist(@PathVariable("playlistId") final String playlistId,
                                  @PathVariable("songId") final String songId) {

        playlistRestService.addSongToPlaylist(playlistId, songId);
    }
}
