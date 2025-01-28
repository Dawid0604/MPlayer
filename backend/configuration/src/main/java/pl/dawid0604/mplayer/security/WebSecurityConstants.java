package pl.dawid0604.mplayer.security;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

final class WebSecurityConstants {
    private WebSecurityConstants() { }

    public static final String COOKIE_NAME = "JSESSIONID";
    public static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    public static final String LOGOUT_ENDPOINT = "/api/v1/auth/logout";
    public static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:4200");
    public static final List<String> ALLOWED_HEADERS = List.of(CONTENT_TYPE, AUTHORIZATION);
    public static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
    public static final String[] ENDPOINTS_WHITELIST = {
            LOGIN_ENDPOINT, LOGOUT_ENDPOINT, "/api/v1/auth/register"
    };
}
