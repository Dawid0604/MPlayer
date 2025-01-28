package pl.dawid0604.mplayer.playlist;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static pl.dawid0604.mplayer.constants.AppConstants.API_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + "playlist")
public class PlaylistRestController {
    private final PlaylistRestService playlistRestService;
}
