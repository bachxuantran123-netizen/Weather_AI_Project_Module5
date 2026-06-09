package com.example.weather_ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "alias", length = 50)
    private String alias;

    @Column(name = "is_primary")
    private boolean isPrimary;

    public AccountLocation(Account account, Location location, String alias) {
        this.account = account;
        this.location = location;
        this.alias = alias;
        this.isPrimary = false;
    }
}