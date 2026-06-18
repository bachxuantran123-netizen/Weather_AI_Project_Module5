package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.ApiResponse;
import com.example.weather_ai.entity.CommunityReport;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.repository.CommunityReportRepository;
import com.example.weather_ai.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AccountRepository accountRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final CommunityReportRepository communityReportRepository;

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
        response.put("aiAdvicesGenerated", totalRequests);
        response.put("chartLabels", chartLabels);
        response.put("chartData", chartData);
        
        return ResponseEntity.ok(com.example.weather_ai.dto.ApiResponse.success("Success", response));
    }

    // API: Đảo ngược trạng thái khóa tài khoản (Khóa thành Mở, Mở thành Khóa)
    @PutMapping("/{id}/toggle-lock")
    public ResponseEntity<?> toggleUserLock(@PathVariable Long id) {
        return accountRepository.findById(id).map(account -> {
            account.setActive(!account.isActive());
            accountRepository.save(account);

            String status = account.isActive() ? "đã được MỞ KHÓA" : "đã BỊ KHÓA";
            return ResponseEntity.ok("Tài khoản " + account.getUsername() + " " + status + " thành công!");

        }).orElseGet(() -> ResponseEntity.badRequest().body("Không tìm thấy người dùng!"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return accountRepository.findById(id).map(account -> {
            account.setActive(false);
            accountRepository.save(account);

            return ResponseEntity.ok("Đã xóa tài khoản thành công!");
        }).orElseGet(() -> ResponseEntity.badRequest().body("Không tìm thấy người dùng!"));
    }

    @GetMapping("/community")
    public ResponseEntity<?> getAllCommunityReports() {
        // Lấy tất cả báo cáo, sắp xếp mới nhất lên đầu
        List<CommunityReport> reports =
                communityReportRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "reportTime"));

        List<Map<String, Object>> responseData = reports.stream().map(r -> Map.<String, Object>of(
                "id", r.getId(),
                "username", r.getAccount().getUsername(),
                "cityName", r.getCityName(),
                "reportType", r.getReportType(),
                "description", r.getDescription(),
                "time", r.getReportTime().toString()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(new com.example.weather_ai.dto.ApiResponse(true, "Thành công", responseData));
    }

    @DeleteMapping("/community/{id}")
    public ResponseEntity<?> deleteCommunityReport(@PathVariable Long id) {
        try {
            communityReportRepository.deleteById(id);
            return ResponseEntity.ok(new com.example.weather_ai.dto.ApiResponse(true, "Đã xóa báo cáo thành công!", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new com.example.weather_ai.dto.ApiResponse(false, "Lỗi khi xóa báo cáo", null));
        }
    }
}