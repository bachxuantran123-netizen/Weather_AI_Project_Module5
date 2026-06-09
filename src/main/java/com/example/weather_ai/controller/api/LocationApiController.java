package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.ApiResponse;
import com.example.weather_ai.dto.LocationRequest;
import com.example.weather_ai.dto.LocationResponse;
import com.example.weather_ai.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationApiController {

    private final LocationService locationService;

    // API thêm địa điểm yêu thích
    @PostMapping
    public ResponseEntity<ApiResponse<String>> addLocation(@RequestBody LocationRequest request, Principal principal) {
        // tự động lấy ra username từ JWT đang gửi kèm trong Header
        String username = principal.getName();
        String result = locationService.addFavoriteLocation(username, request);
        return ResponseEntity.ok(ApiResponse.success(result, null));
    }

    // API lấy danh sách địa điểm yêu thích của User đang đăng nhập
    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getMyLocations(Principal principal) {
        String username = principal.getName();
        List<LocationResponse> myLocations = locationService.getMyLocations(username);
        return ResponseEntity.ok(ApiResponse.success("Success", myLocations));
    }
    // API : Sửa tên gợi nhớ (Update)
    // Đường dẫn: PUT http://localhost:8080/api/v1/locations/{id}?alias=TênMới
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateLocationAlias(
            @PathVariable Long id,
            @RequestParam String alias,
            Principal principal) {

        String username = principal.getName();
        String result = locationService.updateLocationAlias(username, id, alias);
        return ResponseEntity.ok(ApiResponse.success(result, null));
    }

    // API : Xóa địa điểm yêu thích (Delete)
    // Đường dẫn: DELETE http://localhost:8080/api/v1/locations/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> removeLocation(@PathVariable Long id, Principal principal) {

        String username = principal.getName();
        String result = locationService.removeFavoriteLocation(username, id);
        return ResponseEntity.ok(ApiResponse.success(result, null));
    }
}