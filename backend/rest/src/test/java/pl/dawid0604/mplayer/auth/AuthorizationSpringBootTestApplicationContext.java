package pl.dawid0604.mplayer.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.context.annotation.FilterType.REGEX;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "pl.dawid0604.mplayer.auth",
        "pl.dawid0604.mplayer.handler"

}, excludeFilters = {
        @ComponentScan.Filter(type = REGEX, pattern = ".*Test.*")
})
class AuthorizationSpringBootTestApplicationContext {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
