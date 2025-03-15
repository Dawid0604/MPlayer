package pl.dawid0604.mplayer.song;

public record SongGenreDTO(String encryptedId, String name, String color) {
    public SongGenreDTO() {
        this(null, null, null);
    }
}
