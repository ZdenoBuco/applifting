package com.example.applifting.controllers;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.InDTOs.MonitoredEndpointInDTO;
import com.example.applifting.models.OutDTOs.MonitoredEndpointOutDTO;
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

    @GetMapping("/{monitoredEndpointId}")
    public ResponseEntity<MonitoredEndpointOutDTO> getMonitoredEndpoint(@PathVariable UUID monitoredEndpointId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(monitoredEndpointService.getEndpoint(monitoredEndpointId));
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 400);
        }
    }

    @GetMapping()
    public ResponseEntity<List<MonitoredEndpointOutDTO>> getMonitoredEndpoints(@RequestParam(required = false) Integer limit) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(monitoredEndpointService.getEndpoints(limit));
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 400);
        }
    }

    @PostMapping()
    public ResponseEntity<MonitoredEndpointOutDTO> createMonitoredEndpoint(@RequestBody MonitoredEndpointInDTO monitoredEndpointInDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(monitoredEndpointService.createEndpoint(monitoredEndpointInDTO));
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 400);
        }
    }

    @PutMapping("/{monitoredEndpointId}")
    public ResponseEntity<MonitoredEndpointOutDTO> updateMonitoredEndpoint(@PathVariable UUID monitoredEndpointId, @RequestBody MonitoredEndpointInDTO monitoredEndpointInDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(monitoredEndpointService.updateEndpoint(monitoredEndpointInDTO, monitoredEndpointId));
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 400);
        }
    }

    @DeleteMapping("/{monitoredEndpointId}")
    public ResponseEntity<MonitoredEndpointOutDTO> deleteMonitoredEndpoint(@PathVariable UUID monitoredEndpointId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(monitoredEndpointService.deleteEndpoint(monitoredEndpointId));
        } catch (AppliftingException e) {
            throw e;
        } catch (Exception e) {
            throw new AppliftingException(e.getMessage(), 400);
        }
    }
}
