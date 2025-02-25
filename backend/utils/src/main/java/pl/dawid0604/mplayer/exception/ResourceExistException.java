package pl.dawid0604.mplayer.exception;

public class ResourceExistException extends RuntimeException {
    private ResourceExistException(final String message) {
        super(message);
    }

    public static ResourceExistException userUsernameException(final String username) {
        return new ResourceExistException("User[Username='" + username + "'] exists");
    }

    public static ResourceExistException userNicknameException(final String nickname) {
        return new ResourceExistException("User[Nickname='" + nickname + "'] exists");
    }

    public static ResourceExistException userHasPlaylistWithGivenName(final String playlistName) {
        return new ResourceExistException("User playlist[Name='" + playlistName + "'] already exists");
    }

    public static ResourceExistException incomingPasswordIsSameAsOriginal() {
        return new ResourceExistException("Incoming password is same as original");
    }
}
