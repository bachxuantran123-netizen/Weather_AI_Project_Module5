package com.example.weather_ai.config;

import com.example.weather_ai.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/auth/login", "/auth/register", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("phoneNumber")
                        .passwordParameter("password")
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                        .defaultSuccessUrl("/users", true)
                )
                .rememberMe(remember -> remember
                        .key("superSecretKeyForWeatherApp")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .rememberMeParameter("rememberMe")
                        .userDetailsService(customUserDetailsService)
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .permitAll()
                        .logoutSuccessUrl("/auth/login")
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/auth/login?error=denied")
                );

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}