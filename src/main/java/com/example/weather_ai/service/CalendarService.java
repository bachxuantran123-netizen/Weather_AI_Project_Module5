package com.example.weather_ai.service;

import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public String getTodaysEvents(Account account) {
        if (account.getGoogleAccessToken() == null) {
            return "Người dùng chưa liên kết Google Calendar. Không có lịch trình.";
        }

        if (account.getGoogleTokenExpiry() != null && System.currentTimeMillis() > account.getGoogleTokenExpiry()) {
            boolean refreshed = refreshGoogleAccessToken(account);
            if (!refreshed) {
                return "Token đã hết hạn và không thể lấy mới. Hãy yêu cầu người dùng đăng nhập lại.";
            }
        }

        try {
            ZonedDateTime startOfDay = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"));
            ZonedDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

            ObjectMapper objectMapper = new ObjectMapper();

            String timeMin = startOfDay.toInstant().toString();
            String timeMax = endOfDay.toInstant().toString();

            URI uri = UriComponentsBuilder.fromUriString("https://www.googleapis.com/calendar/v3/calendars/primary/events")
                    .queryParam("timeMin", timeMin)
                    .queryParam("timeMax", timeMax)
                    .queryParam("singleEvents", "true")
                    .queryParam("orderBy", "startTime")
                    .build()
                    .encode()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(account.getGoogleAccessToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode items = rootNode.path("items");

            if (items.isMissingNode() || !items.isArray() || items.size() == 0) {
                return "Hôm nay không có sự kiện nào trong lịch trình.";
            }

            StringBuilder eventsBuilder = new StringBuilder("Lịch trình hôm nay của tôi:\n");
            for (JsonNode item : items) {
                String summary = item.path("summary").asText("Sự kiện không tên");

                JsonNode startNode = item.path("start");
                String startTime = startNode.has("dateTime") ? startNode.path("dateTime").asText() : "Cả ngày";

                eventsBuilder.append("- Bắt đầu lúc ").append(startTime).append(": ").append(summary).append("\n");
            }

            return eventsBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi khi đồng bộ lịch trình.";
        }
    }

    // --- Hàm phụ trợ: Làm mới Token ngầm ---
    private boolean refreshGoogleAccessToken(Account account) {
        if (account.getGoogleRefreshToken() == null) return false;

        try {
            String url = "https://oauth2.googleapis.com/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("refresh_token", account.getGoogleRefreshToken());
            body.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String newAccessToken = response.getBody().path("access_token").asText();
                long expiresIn = response.getBody().path("expires_in").asLong();

                account.setGoogleAccessToken(newAccessToken);
                account.setGoogleTokenExpiry(System.currentTimeMillis() + (expiresIn * 1000));
                accountRepository.save(account);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi Refresh Token Google: " + e.getMessage());
        }
        return false;
    }
}