package pl.dawid0604.mplayer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;
import static pl.dawid0604.mplayer.security.WebSecurityConstants.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final ObjectMapper objectMapper;
    private final UserDetailsServiceCustomImpl userDetailsServiceCustomImpl;
    private final AuthenticationEntryPointCustomImpl authenticationEntryPointCustomImpl;

    @Bean
    @DependsOn("authenticationManager")
    public SecurityFilterChain httpSecurity(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.userDetailsService(userDetailsServiceCustomImpl)
                           .exceptionHandling(this::customize)
                           .csrf(AbstractHttpConfigurer::disable)
                           .cors(withDefaults())
                           .logout(this::customize)
                           .authorizeHttpRequests(this::customize)
                           .sessionManagement(this::customize)
                           .securityContext(this::customize)
                           .addFilterAt(new AuthenticationFilterCustomImpl(authenticationManager(httpSecurity), objectMapper), UsernamePasswordAuthenticationFilter.class)
                           .build();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
                                cookieSerializer.setCookieName(COOKIE_NAME);
                                cookieSerializer.setUseHttpOnlyCookie(false);
                                cookieSerializer.setCookiePath("/");
        return cookieSerializer;
    }

    @Bean
    public AuthenticationManager authenticationManager(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                           .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
                          corsConfiguration.setAllowedOrigins(ALLOWED_ORIGINS);
                          corsConfiguration.setAllowedHeaders(ALLOWED_HEADERS);
                          corsConfiguration.setAllowedMethods(ALLOWED_METHODS);
                          corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                                        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    private void customize(final SecurityContextConfigurer<HttpSecurity> config) {
        config.securityContextRepository(new HttpSessionSecurityContextRepository());
    }

    private void customize(final LogoutConfigurer<HttpSecurity> config) {
        config.logoutUrl(LOGOUT_ENDPOINT);
        config.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(COOKIES)));
        config.deleteCookies(COOKIE_NAME);
        config.logoutSuccessHandler((_request, _response, _authentication) -> _response.setStatus(SC_OK));
    }

    private void customize(final SessionManagementConfigurer<HttpSecurity> config) {
        config.maximumSessions(1).maxSessionsPreventsLogin(true);
        config.sessionFixation(SessionFixationConfigurer::newSession);
        config.sessionCreationPolicy(STATELESS);
    }

    private void customize(final ExceptionHandlingConfigurer<HttpSecurity> config) {
        config.authenticationEntryPoint(authenticationEntryPointCustomImpl);
    }

    private void customize(final AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry config) {
        config.requestMatchers(ENDPOINTS_WHITELIST).permitAll()
              .anyRequest().authenticated();
    }
}
