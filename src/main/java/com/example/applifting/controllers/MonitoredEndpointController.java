package com.example.applifting.controllers;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.InDTOs.MonitoredEndpointInDTO;
import com.example.applifting.models.OutDTOs.MonitoredEndpointOutDTO;
import com.example.applifting.servicies.DynamicMonitoringService;
import com.example.applifting.servicies.MonitoredEndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/monitoredEndpoint")
@RequiredArgsConstructor
public class MonitoredEndpointController {
    private final MonitoredEndpointService monitoredEndpointService;
    private final DynamicMonitoringService dynamicMonitoringService;

    @GetMapping("/{monitoredEndpointId}")
    public ResponseEntity<MonitoredEndpointOutDTO> getMonitoredEndpoint(@PathVariable UUID monitoredEndpointId, @RequestParam(required = false) Integer resultLimit) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(monitoredEndpointService.getEndpoint(monitoredEndpointId, resultLimit));
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 400);
        }
    }

    @GetMapping()
    public ResponseEntity<List<MonitoredEndpointOutDTO>> getMonitoredEndpoints(@RequestParam(required = false) Integer resultLimit) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(monitoredEndpointService.getEndpoints(resultLimit));
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 500);
        }
    }

    @PostMapping()
    public ResponseEntity<MonitoredEndpointOutDTO> createMonitoredEndpoint(@RequestBody MonitoredEndpointInDTO monitoredEndpointInDTO) {
        try {
            MonitoredEndpointOutDTO createdEndpoint = monitoredEndpointService.createEndpoint(monitoredEndpointInDTO);
            dynamicMonitoringService.updateMonitoringTask(createdEndpoint);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEndpoint);
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 500);
        }
    }

    @PutMapping("/{monitoredEndpointId}")
    public ResponseEntity<MonitoredEndpointOutDTO> updateMonitoredEndpoint(@PathVariable UUID monitoredEndpointId, @RequestBody MonitoredEndpointInDTO monitoredEndpointInDTO) {
        try {
            MonitoredEndpointOutDTO updatedEndpoint = monitoredEndpointService.updateEndpoint(monitoredEndpointInDTO, monitoredEndpointId);
            dynamicMonitoringService.updateMonitoringTask(updatedEndpoint);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedEndpoint);
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 500);
        }
    }

    @DeleteMapping("/{monitoredEndpointId}")
    public ResponseEntity<MonitoredEndpointOutDTO> deleteMonitoredEndpoint(@PathVariable UUID monitoredEndpointId) {
        try {
            dynamicMonitoringService.removeMonitoringTask(monitoredEndpointId);
            MonitoredEndpointOutDTO deletedEndpoint = monitoredEndpointService.deleteEndpoint(monitoredEndpointId);
            return ResponseEntity.status(HttpStatus.OK).body(deletedEndpoint);
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 500);
        }
    }
}
