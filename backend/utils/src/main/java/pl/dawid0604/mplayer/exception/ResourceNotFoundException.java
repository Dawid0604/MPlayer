package pl.dawid0604.mplayer.exception;

public class ResourceNotFoundException extends RuntimeException {
    private ResourceNotFoundException(final String message) {
        super(message);
    }

    public static ResourceNotFoundException playlistException(final long id) {
        return new ResourceNotFoundException("Playlist[Id=" + id + "] not found");
    }

    public static ResourceNotFoundException songException(final long id) {
        return new ResourceNotFoundException("Song[Id=" + id + "] not found");
    }

    public static ResourceNotFoundException playlistSongException(final long playlistId, final long songId) {
        return new ResourceNotFoundException("Pair[Playlist[Id=" + playlistId + "], Song[Id=" + songId + "]] not found");
    }

    public static ResourceNotFoundException userRoleException(final String role) {
        return new ResourceNotFoundException("UserRole[Role='" + role + "'] not found");
    }
}
