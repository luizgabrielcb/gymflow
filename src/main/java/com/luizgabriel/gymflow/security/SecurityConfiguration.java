package com.luizgabriel.gymflow.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(getAuthorizeHttpRequests())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> getAuthorizeHttpRequests() {
        return auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/v1/users/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "v1/physical-assessments/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "v1/physical-assessments/user/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "v1/physical-assessments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "v1/physical-assessments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "v1/physical-assessments/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "v1/exercises").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "v1/exercises/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "v1/exercises").hasRole("ADMIN")
                .anyRequest().authenticated();
    }
}
