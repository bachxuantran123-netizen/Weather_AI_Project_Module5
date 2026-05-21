package com.example.weather_ai.controller.api;

import com.example.weather_ai.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminApiController {

    private final AccountRepository accountRepository;

    // API: Đảo ngược trạng thái khóa tài khoản (Khóa thành Mở, Mở thành Khóa)
    @PutMapping("/{id}/toggle-lock")
    public ResponseEntity<?> toggleUserLock(@PathVariable Long id) {
        return accountRepository.findById(id).map(account -> {

            // Đảo ngược trạng thái hiện tại
            account.setActive(!account.isActive());
            accountRepository.save(account);

            String status = account.isActive() ? "đã được MỞ KHÓA" : "đã BỊ KHÓA";
            return ResponseEntity.ok("Tài khoản " + account.getUsername() + " " + status + " thành công!");

        }).orElseGet(() -> ResponseEntity.badRequest().body("Không tìm thấy người dùng!"));
    }
}