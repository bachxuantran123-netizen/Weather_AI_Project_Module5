package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.ApiResponse;
import com.example.weather_ai.dto.CommunityReportRequest;
import com.example.weather_ai.entity.Account;
import com.example.weather_ai.entity.CommunityReport;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.repository.CommunityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityApiController {

    private final CommunityReportRepository reportRepository;
    private final AccountRepository accountRepository;

    // 1. API: Người dùng gửi báo cáo lên
    @PostMapping("/reports")
    public ResponseEntity<?> createReport(@RequestBody CommunityReportRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "Vui lòng đăng nhập để báo cáo!", null));
        }

        Account account = accountRepository.findByUsername(principal.getName()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "Không tìm thấy tài khoản!", null));
        }

        CommunityReport report = CommunityReport.builder()
                .account(account)
                .cityName(request.getCityName())
                .reportType(request.getReportType())
                .description(request.getDescription())
                .build();

        reportRepository.save(report);
        return ResponseEntity.ok(new ApiResponse(true, "Đã gửi báo cáo thành công!", null));
    }

    // 2. API: Lấy danh sách báo cáo theo thành phố
    @GetMapping("/reports")
    public ResponseEntity<?> getReportsByCity(@RequestParam String city) {
        List<CommunityReport> reports = reportRepository.findByCityNameOrderByReportTimeDesc(city);

        List<Map<String, Object>> responseData = reports.stream().map(r -> Map.of(
                "id", (Object) r.getId(),
                "username", r.getAccount().getUsername(), // Lấy tên người đăng
                "reportType", r.getReportType(),
                "description", r.getDescription(),
                "time", r.getReportTime().toString()
        )).toList();

        return ResponseEntity.ok(new ApiResponse(true, "Thành công", responseData));
    }
}