package pl.dawid0604.mplayer.song;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + "song")
public class SongRestController {
    private final SongRestService songRestService;
    private final SongGenreRestService songGenreRestService;
    private final SongMoodRestService songMoodRestService;

    @ResponseStatus(OK)
    @GetMapping("/find/welcome")
    public WelcomeSongsDTO findWelcomeSongs() {
        return songRestService.findWelcomeSongs();
    }

    @ResponseStatus(OK)
    @PostMapping("/discover")
    public DiscoverSongsDTO discover(@RequestBody @Valid final DiscoverPayload payload) {
        return songRestService.discover(payload);
    }

    @ResponseStatus(OK)
    @PatchMapping("/listening/{encryptedId}/handle")
    public void handleSongListening(@PathVariable("encryptedId") final String encryptedId) {
        songRestService.handleSongListening(encryptedId);
    }

    @ResponseStatus(OK)
    @GetMapping("/genres")
    public List<SongGenreDTO> findGenres() {
        return songGenreRestService.findAll();
    }

    @ResponseStatus(OK)
    @GetMapping("/moods")
    public List<SongMoodDTO> findMoods() {
        return songMoodRestService.findAll();
    }
}
