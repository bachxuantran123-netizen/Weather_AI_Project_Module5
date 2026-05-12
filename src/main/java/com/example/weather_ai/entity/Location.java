package com.example.weather_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cityName;

    @Column(nullable = false)
    private Double latitude; // Vĩ độ

    @Column(nullable = false)
    private Double longitude; // Kinh độ
}