package com.example.applifting.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonitoringResult {
    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime checkedAt;
    private Integer httpCode;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private UUID monitoredEndpointId;
}
