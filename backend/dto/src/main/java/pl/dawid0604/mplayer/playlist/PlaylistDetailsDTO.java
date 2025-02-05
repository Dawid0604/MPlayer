package pl.dawid0604.mplayer.playlist;

import pl.dawid0604.mplayer.song.SongDTO;

import java.util.List;

public record PlaylistDetailsDTO(PlaylistDTO playlist, List<SongDTO> songs) { }
