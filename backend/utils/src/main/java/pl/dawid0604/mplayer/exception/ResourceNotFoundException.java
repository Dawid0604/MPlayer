package pl.dawid0604.mplayer.exception;

public class ResourceNotFoundException extends RuntimeException {
    private ResourceNotFoundException(final String message) {
        super(message);
    }

    public static ResourceNotFoundException playlistNotFoundException(final long id) {
        return new ResourceNotFoundException("Playlist[Id=" + id + "] not found");
    }

    public static ResourceNotFoundException songNotFoundException(final long id) {
        return new ResourceNotFoundException("Song[Id=" + id + "] not found");
    }

    public static ResourceNotFoundException playlistSongNotFoundException(final long playlistId, final long songId) {
        return new ResourceNotFoundException("Pair[Playlist[Id=" + playlistId + "], Song[Id=" + songId + "]] not found");
    }

    public static ResourceNotFoundException userRoleNotFoundException(final String role) {
        return new ResourceNotFoundException("UserRole[Role='" + role + "'] not found");
    }

    public static ResourceNotFoundException userHasPlaylistWithGivenName(final String playlistName) {
        return new ResourceNotFoundException("User playlist[Name='" + playlistName + "'] already exists");
    }
}
