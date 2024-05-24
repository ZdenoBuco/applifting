package com.example.applifting.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String email;
    private String accessToken;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<MonitoredEndpoint> monitoredEndpoints;
}
