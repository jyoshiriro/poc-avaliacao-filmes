package ada.apiavaliacaofilmes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UsuariosConfig {

    @Bean
    public UserDetailsService users() {
        UserDetails user1 = User.builder()
                .username("jogador1")
                .password("{noop}s1")
                .roles("jogador")
                .build();

        UserDetails user2 = User.builder()
                .username("jogador2")
                .password("{noop}s2")
                .roles("jogador")
                .build();

        UserDetails user3 = User.builder()
                .username("admin")
                .password("{noop}admin")
                .roles("expectador")
                .build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                        "/h2-console/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                        );
    }
}
