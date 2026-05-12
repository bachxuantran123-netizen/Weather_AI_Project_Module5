package com.example.weather_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account_locations")
public class AccountLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BẮT BUỘC dùng FetchType.LAZY để tránh N+1 Query Problem
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // BẮT BUỘC dùng FetchType.LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "alias", length = 50)
    private String alias; // Nơi lưu "Nhà", "Công ty", "Trường học"

    @Column(name = "is_primary")
    private boolean isPrimary; // Đánh dấu vị trí mặc định hiển thị đầu tiên trên App

    public AccountLocation(Account account, Location location, String alias) {
        this.account = account;
        this.location = location;
        this.alias = alias;
        this.isPrimary = false;
    }
}
