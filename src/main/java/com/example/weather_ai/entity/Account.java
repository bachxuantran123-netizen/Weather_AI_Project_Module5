package com.example.weather_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private String role = "USER";

    @Column(name = "fcm_device_token", length = 512)
    private String fcmDeviceToken;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountLocation> trackedLocations = new ArrayList<>();

    public void addLocation(Location location, String alias) {
        AccountLocation accountLocation = new AccountLocation(this, location, alias);
        trackedLocations.add(accountLocation);
    }
}