package com.art.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers("/api/teachers/**", "/api/students/**",
                                 "/api/courses/**", "/api/classrooms/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/classes/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/classes/**").hasRole("ADMIN")
                .requestMatchers("/api/adjustments/*/approve", "/api/adjustments/*/reject").hasRole("ADMIN")
                .requestMatchers("/api/adjustments/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/schedules/manual", "/api/schedules/auto").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/schedules/**").authenticated()
                .requestMatchers("/api/dashboard/**").authenticated()
                .requestMatchers("/api/auth/me", "/api/auth/logout").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
