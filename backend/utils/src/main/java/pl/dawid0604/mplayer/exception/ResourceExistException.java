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
}
