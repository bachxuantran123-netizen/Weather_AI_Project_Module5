package com.example.weather_ai.service;

import com.example.weather_ai.dto.LocationRequest;
import com.example.weather_ai.dto.LocationResponse;
import com.example.weather_ai.entity.Account;
import com.example.weather_ai.entity.AccountLocation;
import com.example.weather_ai.entity.Location;
import com.example.weather_ai.repository.AccountLocationRepository;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final AccountRepository accountRepository;
    private final LocationRepository locationRepository;
    private final AccountLocationRepository accountLocationRepository;

    @Transactional // Đảm bảo tính toàn vẹn dữ liệu (Nếu lỗi sẽ rollback toàn bộ)
    public String addFavoriteLocation(String username, LocationRequest request) {
        // 1. Lấy thông tin user đang đăng nhập
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản."));

        // 2. Tìm Location trong Database, nếu chưa có thì tạo mới và lưu lại
        Location location = locationRepository.findByCityName(request.getCityName())
                .orElseGet(() -> {
                    Location newLocation = new Location();
                    newLocation.setCityName(request.getCityName());
                    newLocation.setLatitude(request.getLatitude());
                    newLocation.setLongitude(request.getLongitude());
                    return locationRepository.save(newLocation);
                });

        // 3. Kiểm tra xem User này đã theo dõi địa điểm này chưa (tránh thêm trùng lặp)
        boolean alreadyTracked = account.getTrackedLocations().stream()
                .anyMatch(al -> al.getLocation().getId().equals(location.getId()));

        if (alreadyTracked) {
            return "Địa điểm này đã có trong danh sách yêu thích của bạn!";
        }

        // 4. Liên kết Location với Account qua hàm Helper bạn đã viết sẵn trong Entity
        account.addLocation(location, request.getAlias());
        accountRepository.save(account);

        return "Thêm địa điểm yêu thích thành công!";
    }

    public List<LocationResponse> getMyLocations(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản."));

        // Lấy danh sách từ bảng trung gian và chuyển đổi sang DTO để trả về
        List<AccountLocation> trackedLocations = accountLocationRepository.findByAccountId(account.getId());

        return trackedLocations.stream()
                .map(al -> new LocationResponse(
                        al.getId(),
                        al.getLocation().getCityName(),
                        al.getAlias(),
                        al.isPrimary()
                ))
                .collect(Collectors.toList());
    }
    // API 3: Sửa tên gợi nhớ (Update Alias)
    @Transactional
    public String updateLocationAlias(String username, Long accountLocationId, String newAlias) {
        // Kiểm tra xem AccountLocation có tồn tại không
        AccountLocation accountLocation = accountLocationRepository.findById(accountLocationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm yêu thích này."));

        // BẢO MẬT: Kiểm tra xem địa điểm này có đúng là của User đang gọi API không
        if (!accountLocation.getAccount().getUsername().equals(username)) {
            throw new RuntimeException("🚨 BẢO MẬT: Bạn không có quyền sửa địa điểm của người khác!");
        }

        // Cập nhật tên mới
        accountLocation.setAlias(newAlias);
        accountLocationRepository.save(accountLocation);

        return "Cập nhật tên gợi nhớ thành công!";
    }

    // API 4: Xóa địa điểm yêu thích (Delete)
    @Transactional
    public String removeFavoriteLocation(String username, Long accountLocationId) {
        AccountLocation accountLocation = accountLocationRepository.findById(accountLocationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm yêu thích này."));

        // BẢO MẬT: Chỉ chủ sở hữu mới được xóa
        if (!accountLocation.getAccount().getUsername().equals(username)) {
            throw new RuntimeException("🚨 BẢO MẬT: Bạn không có quyền xóa địa điểm của người khác!");
        }

        // Chỉ xóa liên kết trong bảng trung gian AccountLocation
        accountLocationRepository.delete(accountLocation);

        return "Đã xóa địa điểm khỏi danh sách yêu thích.";
    }
}