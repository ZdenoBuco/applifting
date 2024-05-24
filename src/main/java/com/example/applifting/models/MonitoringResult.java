package com.example.applifting.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringResult {
    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime checkedAt;
    private Integer httpCode;
    private String payload;
    @ManyToOne
    private MonitoredEndpoint monitoredEndpoint;;
}
