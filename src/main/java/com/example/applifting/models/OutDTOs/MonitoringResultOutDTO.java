package com.example.applifting.models.OutDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringResultOutDTO {
    private UUID id;
    private LocalDateTime checkedAt;
    private Integer httpCode;
    private String payload;
}
