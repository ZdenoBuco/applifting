package com.example.applifting.utilities;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.InDTOs.MonitoredEndpointInDTO;

public class MonitoredEndpointInDTOValidator {
    public static final int NAME_MAX_LENGTH = 255;
    public static final int URL_MAX_LENGTH = 255;
    public static final int MONITORING_INTERVAL_MIN = 5;
    public static final int MONITORING_INTERVAL_MAX = 100_000;

    public static void validate(MonitoredEndpointInDTO monitoredEndpointInDTO) {
        if (monitoredEndpointInDTO.getName() == null || monitoredEndpointInDTO.getName().length() > NAME_MAX_LENGTH) {
            throw new AppliftingException("Endpoint name cannot be null and must be up to " + NAME_MAX_LENGTH + " characters long", 400);
        }
        if (monitoredEndpointInDTO.getUrl() == null || monitoredEndpointInDTO.getUrl().length() > URL_MAX_LENGTH) {
            throw new AppliftingException("Endpoint URL cannot be null and must be up to " + URL_MAX_LENGTH + " characters long", 400);
        }
        if (monitoredEndpointInDTO.getMonitoringInterval() == null ||
                monitoredEndpointInDTO.getMonitoringInterval() > MONITORING_INTERVAL_MAX ||
                monitoredEndpointInDTO.getMonitoringInterval() < MONITORING_INTERVAL_MIN) {
            throw new AppliftingException("Endpoint monitoring interval cannot be null and must be between " + MONITORING_INTERVAL_MIN + " and " + MONITORING_INTERVAL_MAX, 400);
        }
    }
    public static void validateWithoutUrl(MonitoredEndpointInDTO monitoredEndpointInDTO) {
        if (monitoredEndpointInDTO.getName() == null || monitoredEndpointInDTO.getName().length() > NAME_MAX_LENGTH) {
            throw new AppliftingException("Endpoint name cannot be null and must be up to " + NAME_MAX_LENGTH + " characters long", 400);
        }
        if (monitoredEndpointInDTO.getMonitoringInterval() == null ||
                monitoredEndpointInDTO.getMonitoringInterval() > MONITORING_INTERVAL_MAX ||
                monitoredEndpointInDTO.getMonitoringInterval() < MONITORING_INTERVAL_MIN) {
            throw new AppliftingException("Endpoint monitoring interval cannot be null and must be between " + MONITORING_INTERVAL_MIN + " and " + MONITORING_INTERVAL_MAX, 400);
        }
    }
}
