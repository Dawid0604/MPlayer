package pl.dawid0604.mplayer.song;

public interface SongRestService {
    WelcomeSongsDTO findWelcomeSongs();

    void handleSongListening(String encryptedId);

    DiscoverSongsDTO discover(DiscoverPayload payload);
}
