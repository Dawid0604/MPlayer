package pl.dawid0604.mplayer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static pl.dawid0604.mplayer.security.WebSecurityConstants.LOGIN_ENDPOINT;

public class AuthenticationFilterCustomImpl extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private record LoginPayload(String username, String password) { }

    public AuthenticationFilterCustomImpl(final AuthenticationManager authenticationManager, final ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl(LOGIN_ENDPOINT);
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        try {
            LoginPayload payload = objectMapper.readValue(request.getInputStream(), LoginPayload.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.username(), payload.password()));

        } catch (Exception exception) {
            throw new AuthenticationServiceException("Invalid login payload", exception);
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final FilterChain chain, final Authentication authResult) {

        SecurityContextHolder.getContext().setAuthentication(authResult);
        HttpSessionSecurityContextRepository sessionSecurityContextRepository = new HttpSessionSecurityContextRepository();
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authResult);
        sessionSecurityContextRepository.saveContext(context, request, response);
        response.setStatus(SC_OK);
    }

    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                              final AuthenticationException failed) {

        response.setStatus(SC_UNAUTHORIZED);
    }
}
