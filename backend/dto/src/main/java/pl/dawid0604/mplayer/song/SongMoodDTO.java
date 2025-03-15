package pl.dawid0604.mplayer.song;

public record SongMoodDTO(String encryptedId, String name, String color) {
    public SongMoodDTO() {
        this(null, null, null);
    }
}
