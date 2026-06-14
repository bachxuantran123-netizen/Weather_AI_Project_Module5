package com.example.weather_ai.service;

import com.example.weather_ai.entity.Account;
import com.example.weather_ai.entity.SearchHistory;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final AccountRepository accountRepository;

    @Async
    public void logSearchAsync(String username, String cityName) {
        accountRepository.findByUsername(username).ifPresent(account -> {
            SearchHistory history = new SearchHistory();
            history.setAccount(account);
            history.setCityName(cityName);

            searchHistoryRepository.save(history);
            System.out.println("[ASYNC] Đã lưu lịch sử tìm kiếm: " + cityName + " của user: " + username);
        });
    }
}