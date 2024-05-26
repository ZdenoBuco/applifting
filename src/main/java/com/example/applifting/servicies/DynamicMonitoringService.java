package com.example.applifting.servicies;

import com.example.applifting.models.MonitoredEndpoint;
import com.example.applifting.models.MonitoringResult;
import com.example.applifting.models.OutDTOs.MonitoredEndpointOutDTO;
import com.example.applifting.repositories.MonitoredEndpointRepository;
import com.example.applifting.repositories.MonitoringResultRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class DynamicMonitoringService {
    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final MonitoringResultRepository monitoringResultRepository;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
    private final Map<UUID, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(DynamicMonitoringService.class);

    @PostConstruct
    public void init() {
        loadAllMonitoredEndpoints();
    }

    private void loadAllMonitoredEndpoints() {
        List<MonitoredEndpoint> endpoints = monitoredEndpointRepository.findAll();
        for (MonitoredEndpoint endpoint : endpoints) {
            scheduleMonitoringTask(endpoint);
        }
    }

    private void scheduleMonitoringTask(MonitoredEndpoint endpoint) {
        Runnable task = () -> checkEndpoint(endpoint);

        ScheduledFuture<?> future = executorService.scheduleAtFixedRate(task, 0, endpoint.getMonitoringInterval(), TimeUnit.SECONDS);
        tasks.put(endpoint.getId(), future);
    }

    private void checkEndpoint(MonitoredEndpoint endpoint) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(endpoint.getUrl(), String.class);
            createMonitoringResult(endpoint, response.getStatusCode().value(), response.getBody());
            endpoint.setLastCheck(LocalDateTime.now());
            monitoredEndpointRepository.save(endpoint);
            logger.info("Successfully monitored endpoint: {} with status code: {} with payload:\n{}", endpoint.getId(), response.getStatusCode().value(), trimResponseBody(response.getBody()));
        } catch (Exception e) {
            int statusCode = 500;
            try {
                statusCode = Integer.parseInt(e.getMessage().substring(0, 3));

            } catch (NumberFormatException | StringIndexOutOfBoundsException ex) {
                // If parsing failed or substring index out of bounds, use status code 500
            }
            createMonitoringResult(endpoint, statusCode, e.getMessage());
            endpoint.setLastCheck(LocalDateTime.now());
            monitoredEndpointRepository.save(endpoint);
            logger.warn("Error monitoring endpoint: {} with status code: {} with payload:\n{}", endpoint.getId(), statusCode, trimResponseBody(e.getMessage()));
        }
    }

    private void createMonitoringResult(MonitoredEndpoint endpoint, int statusCode, String payload) {
        MonitoringResult result = MonitoringResult.builder()
                .monitoredEndpointId(endpoint.getId())
                .checkedAt(LocalDateTime.now())
                .httpCode(statusCode)
                .payload(payload)
                .build();
        monitoringResultRepository.save(result);
    }

    @Transactional
    public void updateMonitoringTask(MonitoredEndpointOutDTO updatedEndpoint) {
        ScheduledFuture<?> future = tasks.remove(updatedEndpoint.getId());
        if (future != null) {
            future.cancel(true);
        }
        scheduleMonitoringTask(monitoredEndpointRepository.findById(updatedEndpoint.getId()).orElseThrow());
    }

    @Transactional
    public void removeMonitoringTask(UUID endpointId) {
        ScheduledFuture<?> future = tasks.remove(endpointId);
        if (future != null) {
            future.cancel(true);
        }
    }

    private String trimResponseBody(String body) {
        return body == null ? "" : body.trim().replaceAll("[\n\r\t]", " ");
    }
}
