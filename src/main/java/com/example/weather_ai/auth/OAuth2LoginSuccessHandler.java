package com.example.weather_ai.auth;

import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AccountRepository accountRepository;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        Account account = accountRepository.findByUsername(email).orElseGet(() -> {
            Account newAccount = new Account();
            newAccount.setUsername(email);
            newAccount.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newAccount.setRole("USER");
            newAccount.setActive(true);
            return accountRepository.save(newAccount);
        });

        if (!account.isActive()) {
            getRedirectStrategy().sendRedirect(request, response, "/oauth2-redirect?error=locked");
            return;
        }

        String accessToken = jwtUtils.generateTokenFromUsername(account.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(account.getUsername());

        redisTemplate.opsForValue().set(
                "RT_" + account.getUsername(),
                refreshToken,
                jwtUtils.getRefreshExpirationMs(),
                TimeUnit.MILLISECONDS
        );

        String targetUrl = "/oauth2-redirect?token=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}