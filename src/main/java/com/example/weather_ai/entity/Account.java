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

    // Quan hệ 1-N tới bảng trung gian
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountLocation> trackedLocations = new ArrayList<>();

    // Helper method để code ở Service sạch sẽ hơn
    public void addLocation(Location location, String alias) {
        AccountLocation accountLocation = new AccountLocation(this, location, alias);
        trackedLocations.add(accountLocation);
    }
}