package pl.dawid0604.mplayer.song;

import java.util.List;

public record SongDTO(String encryptedId, String title, List<String> authors,
                      String thumbnailPath, String soundLink) { }
