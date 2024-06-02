package com.example.applifting.testData;

import com.example.applifting.models.MonitoredEndpoint;
import com.example.applifting.models.MonitoringResult;
import com.example.applifting.models.OutDTOs.MonitoringResultOutDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MonitoredEndpointServiceTD {
    private static final LocalDateTime NOW = LocalDateTime.now();
    public static List<MonitoringResult> getMonitoringResults(UUID monitoringResultId, UUID monitoredEndpointId) {
        return List.of(
                MonitoringResult.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(0))
                        .httpCode(200)
                        .payload("Payload 1")
                        .monitoredEndpointId(monitoredEndpointId)
                        .build(),
                MonitoringResult.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(10))
                        .httpCode(204)
                        .payload("Payload 2")
                        .monitoredEndpointId(monitoredEndpointId)
                        .build(),
                MonitoringResult.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(20))
                        .httpCode(201)
                        .payload("Payload 3")
                        .monitoredEndpointId(monitoredEndpointId)
                        .build(),
                MonitoringResult.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(30))
                        .httpCode(500)
                        .payload("Payload 4")
                        .monitoredEndpointId(monitoredEndpointId)
                        .build(),
                MonitoringResult.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(40))
                        .httpCode(404)
                        .payload("Payload 5")
                        .monitoredEndpointId(monitoredEndpointId)
                        .build()
        );
    }

    public static List<MonitoringResultOutDTO> getMonitoringResultsOutDTO(UUID monitoringResultId, UUID monitoredEndpointId) {
        return List.of(
                MonitoringResultOutDTO.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(0))
                        .httpCode(200)
                        .payload("Payload 1")
                        .build(),
                MonitoringResultOutDTO.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(10))
                        .httpCode(204)
                        .payload("Payload 2")
                        .build(),
                MonitoringResultOutDTO.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(20))
                        .httpCode(201)
                        .payload("Payload 3")
                        .build(),
                MonitoringResultOutDTO.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(30))
                        .httpCode(500)
                        .payload("Payload 4")
                        .build(),
                MonitoringResultOutDTO.builder()
                        .id(monitoringResultId)
                        .checkedAt(NOW.minusSeconds(40))
                        .httpCode(404)
                        .payload("Payload 5")
                        .build()
        );
    }
    public static List<MonitoredEndpoint> getMonitoredEndpoints(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> MonitoredEndpoint.builder()
                        .id(UUID.randomUUID())
                        .name("Endpoint " + i)
                        .url("http://example.com/endpoint" + i)
                        .createdAt(LocalDateTime.now())
                        .lastCheck(LocalDateTime.now())
                        .monitoringInterval(60)
                        .monitoringResults(null)
                        .ownerId(UUID.randomUUID())
                        .build())
                .collect(Collectors.toList());
    }
}

