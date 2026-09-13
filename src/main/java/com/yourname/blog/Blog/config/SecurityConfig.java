package com.yourname.blog.Blog.config;

import com.yourname.blog.Blog.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()               
                        .requestMatchers("/", "/health", "/api/health", "/api/auth/**", "/api/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articles/mine", "/api/articles/following", "/api/blogs/mine", "/api/users/me").authenticated()
                        .requestMatchers("/api/bookmarks/**", "/api/follows/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/articles/**", "/api/blogs/**", "/api/comments/**", "/api/likes/**", "/api/users/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

