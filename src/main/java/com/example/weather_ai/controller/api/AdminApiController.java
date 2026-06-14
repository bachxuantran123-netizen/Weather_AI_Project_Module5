package com.example.weather_ai.controller.api;

import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AccountRepository accountRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        long totalUsers = accountRepository.count();
        long totalRequests = searchHistoryRepository.count();
        
        java.util.List<Object[]> stats = searchHistoryRepository.countRequestsByDayLast7Days();
        java.util.List<String> chartLabels = new java.util.ArrayList<>();
        java.util.List<Long> chartData = new java.util.ArrayList<>();
        
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
        for (Object[] row : stats) {
            Object dateObj = row[0];
            java.time.LocalDate localDate;
            if (dateObj instanceof java.time.LocalDate) {
                localDate = (java.time.LocalDate) dateObj;
            } else if (dateObj instanceof java.sql.Date) {
                localDate = ((java.sql.Date) dateObj).toLocalDate();
            } else {
                localDate = java.time.LocalDate.parse(dateObj.toString());
            }

            Long count = ((Number) row[1]).longValue();

            chartLabels.add(localDate.format(formatter));
            chartData.add(count);
        }
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("totalUsers", totalUsers);
        response.put("totalRequests", totalRequests);
        response.put("aiAdvicesGenerated", totalRequests); // Fake logic cho bằng với số request
        response.put("chartLabels", chartLabels);
        response.put("chartData", chartData);
        
        return ResponseEntity.ok(com.example.weather_ai.dto.ApiResponse.success("Success", response));
    }

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