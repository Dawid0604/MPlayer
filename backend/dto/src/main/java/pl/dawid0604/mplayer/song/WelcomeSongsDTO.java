package pl.dawid0604.mplayer.song;

import java.util.List;

public record WelcomeSongsDTO(List<SongDTO> popular, List<SongDTO> recentReleases) { }
