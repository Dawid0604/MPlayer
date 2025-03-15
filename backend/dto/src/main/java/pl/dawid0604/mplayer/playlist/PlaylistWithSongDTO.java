package pl.dawid0604.mplayer.playlist;

public record PlaylistWithSongDTO(String encryptedId, String name, boolean songIsPresent) {
    public PlaylistWithSongDTO() {
        this(null, null, false);
    }
}
