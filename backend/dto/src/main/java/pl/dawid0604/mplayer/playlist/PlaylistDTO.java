package pl.dawid0604.mplayer.playlist;

public record PlaylistDTO(String encryptedId, String name, String createdDate,
                          long numberOfSongs) { }
