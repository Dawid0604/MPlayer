package pl.dawid0604.mplayer.user;

public interface UserRestService {
    long getLoggedUserId();

    String getLoggedUserUsername();

    void register(String username, String password, String nickname);
}
