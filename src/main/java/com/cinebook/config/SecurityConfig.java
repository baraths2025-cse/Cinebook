
package com.cinebook.config;

import com.cinebook.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository repository,
            DaoAuthenticationProvider provider) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .securityContext(context ->
                context.securityContextRepository(repository))
            .authenticationProvider(provider)

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/",
                    "/index.html",
                    "/*.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
.requestMatchers("/uploads/**").permitAll()
                // Anyone can view movies
                .requestMatchers(HttpMethod.GET, "/api/movies/**")
                .permitAll()

                // Only ADMIN can modify movies
                .requestMatchers("/api/movies/**")
                .hasRole("ADMIN")

                // Anyone can view shows
                .requestMatchers(HttpMethod.GET, "/api/shows/**")
                .permitAll()

                // Only ADMIN can add or delete shows
                .requestMatchers("/api/shows/**")
                .hasRole("ADMIN")

                // All remaining APIs require login
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
