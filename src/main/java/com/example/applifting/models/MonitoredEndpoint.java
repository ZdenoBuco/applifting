package com.example.applifting.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonitoredEndpoint {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime lastCheck;
    private Integer monitoringInterval;
    @OneToMany(mappedBy = "monitoredEndpointId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MonitoringResult> monitoringResults;
    private UUID ownerId;
}
