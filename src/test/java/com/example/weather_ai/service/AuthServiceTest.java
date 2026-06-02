package com.example.weather_ai.service;

import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Kích hoạt Mockito cho JUnit 5
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService; // Tiêm các Mock phía trên vào Service này

    @Test
    @DisplayName("Đăng ký thành công - Mật khẩu được mã hóa và lưu vào DB")
    void givenValidUser_whenRegister_thenAccountSavedSuccessfully() {
        // Given
        String username = "testuser";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        given(accountRepository.existsByUsername(username)).willReturn(false);
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);

        // When
        authService.register(username, rawPassword);

        // Then
        // Xác minh accountRepository.save() đã được gọi với Account có pass đã mã hóa
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Đăng ký thất bại - Username đã tồn tại ném ra Exception")
    void givenExistingUsername_whenRegister_thenThrowException() {
        // Given
        String username = "existingUser";
        String rawPassword = "password123";

        given(accountRepository.existsByUsername(username)).willReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            authService.register(username, rawPassword);
        }, "Error: Username is already taken!");
    }
}