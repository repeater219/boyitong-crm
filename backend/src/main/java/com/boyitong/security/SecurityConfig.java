package com.boyitong.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/uploads/pending", "/api/uploads/all").hasRole("ADMIN")
                .requestMatchers("/api/uploads/*/approve", "/api/uploads/*/reject").hasRole("ADMIN")
                .requestMatchers("/api/uploads/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/customers/import").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers("/api/customers/**").authenticated()
                .requestMatchers("/api/stats").authenticated()
                .requestMatchers("/api/export/**").authenticated()
                .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/crm/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/crm/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/crm/announcements").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/crm/announcements/**").hasRole("ADMIN")
                .requestMatchers("/api/notifications/**").authenticated()
                .requestMatchers("/api/tasks/**").authenticated()
                .requestMatchers("/api/follow-ups/**").authenticated()
                .requestMatchers("/api/crm/**").authenticated()
                .requestMatchers("/api/ai/**").authenticated()
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/", "/index.html", "/static/**", "/*.js", "/*.css", "/*.json", "/*.ico", "/*.png", "/*.svg", "/*.jpg", "/*.jpeg", "/*.woff", "/*.woff2", "/*.ttf", "/*.eot", "/assets/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}