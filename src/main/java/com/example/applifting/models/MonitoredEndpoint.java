package com.example.applifting.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MonitoredEndpoint {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime lastCheck;
    @OneToMany(mappedBy = "monitoredEndpoint", cascade = CascadeType.ALL)
    private List<MonitoringResult> monitoringResults;
    @ManyToOne
    private AppUser owner;
}
