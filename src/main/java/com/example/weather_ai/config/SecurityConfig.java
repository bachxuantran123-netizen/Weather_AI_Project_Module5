package com.example.weather_ai.config;

import com.example.weather_ai.auth.JwtAuthenticationFilter;
import com.example.weather_ai.auth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,  OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ASYNC).permitAll()
                        .requestMatchers("/","/api/auth/**", "/admin-login", "/mobile-preview", "/oauth2-redirect", "/login/oauth2/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );
        http.oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler)
        );
        http.addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
