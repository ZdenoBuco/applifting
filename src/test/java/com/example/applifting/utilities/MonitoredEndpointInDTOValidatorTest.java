package com.example.applifting.utilities;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.InDTOs.MonitoredEndpointInDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonitoredEndpointInDTOValidatorTest {
    private final static String ENDPOINT_NAME = "endpoint";
    private final static String ENDPOINT_URL = "http://url.url";
    private final static Integer ENDPOINT_INTERVAL = 10;

    @Test
    void validate_withValidInput_doesNotThrowException() {
        MonitoredEndpointInDTO validEndpoint = new MonitoredEndpointInDTO();
        validEndpoint.setName(ENDPOINT_NAME);
        validEndpoint.setUrl(ENDPOINT_URL);
        validEndpoint.setMonitoringInterval(ENDPOINT_INTERVAL);

        assertDoesNotThrow(() -> MonitoredEndpointInDTOValidator.validate(validEndpoint));
    }

    @Test
    void validate_withNullName_throwsException() {
        MonitoredEndpointInDTO endpointWithNullName = new MonitoredEndpointInDTO();
        endpointWithNullName.setName(null);
        endpointWithNullName.setUrl(ENDPOINT_URL);
        endpointWithNullName.setMonitoringInterval(ENDPOINT_INTERVAL);

        assertThrows(AppliftingException.class, () -> MonitoredEndpointInDTOValidator.validate(endpointWithNullName));
    }

    @Test
    void validate_withTooLongName_throwsException() {
        MonitoredEndpointInDTO endpointWithLongName = new MonitoredEndpointInDTO();
        endpointWithLongName.setName("a".repeat(MonitoredEndpointInDTOValidator.NAME_MAX_LENGTH+1));
        endpointWithLongName.setUrl(ENDPOINT_URL);
        endpointWithLongName.setMonitoringInterval(ENDPOINT_INTERVAL);

        assertThrows(AppliftingException.class, () -> MonitoredEndpointInDTOValidator.validate(endpointWithLongName));
    }

    @Test
    void validate_withNullUrl_throwsException() {
        MonitoredEndpointInDTO endpointWithNullUrl = new MonitoredEndpointInDTO();
        endpointWithNullUrl.setName(ENDPOINT_NAME);
        endpointWithNullUrl.setUrl(null);
        endpointWithNullUrl.setMonitoringInterval(ENDPOINT_INTERVAL);

        assertThrows(AppliftingException.class, () -> MonitoredEndpointInDTOValidator.validate(endpointWithNullUrl));
    }

    @Test
    void validate_withTooLongUrl_throwsException() {
        MonitoredEndpointInDTO endpointWithLongUrl = new MonitoredEndpointInDTO();
        endpointWithLongUrl.setName(ENDPOINT_NAME);
        endpointWithLongUrl.setUrl("http://" + "a".repeat(MonitoredEndpointInDTOValidator.URL_MAX_LENGTH-10) + ".com");
        endpointWithLongUrl.setMonitoringInterval(ENDPOINT_INTERVAL);

        assertThrows(AppliftingException.class, () -> MonitoredEndpointInDTOValidator.validate(endpointWithLongUrl));
    }

    @Test
    void validate_withNullMonitoringInterval_throwsException() {
        MonitoredEndpointInDTO endpointWithNullInterval = new MonitoredEndpointInDTO();
        endpointWithNullInterval.setName(ENDPOINT_NAME);
        endpointWithNullInterval.setUrl(ENDPOINT_URL);
        endpointWithNullInterval.setMonitoringInterval(null);

        assertThrows(AppliftingException.class, () -> MonitoredEndpointInDTOValidator.validate(endpointWithNullInterval));
    }

    @Test
    void validate_withTooShortMonitoringInterval_throwsException() {
        MonitoredEndpointInDTO endpointWithShortInterval = new MonitoredEndpointInDTO();
        endpointWithShortInterval.setName(ENDPOINT_NAME);
        endpointWithShortInterval.setUrl(ENDPOINT_URL);
        endpointWithShortInterval.setMonitoringInterval(MonitoredEndpointInDTOValidator.MONITORING_INTERVAL_MIN-1);

        assertThrows(AppliftingException.class, () -> MonitoredEndpointInDTOValidator.validate(endpointWithShortInterval));
    }

    @Test
    void validate_withTooLongMonitoringInterval_throwsException() {
        MonitoredEndpointInDTO endpointWithLongInterval = new MonitoredEndpointInDTO();
        endpointWithLongInterval.setName(ENDPOINT_NAME);
        endpointWithLongInterval.setUrl(ENDPOINT_URL);
        endpointWithLongInterval.setMonitoringInterval(MonitoredEndpointInDTOValidator.MONITORING_INTERVAL_MAX+1);

        assertThrows(AppliftingException.class, () -> MonitoredEndpointInDTOValidator.validate(endpointWithLongInterval));
    }

//    method validateWithoutUrl() is only subset of conditions of validate(), so it's not necessary to test again
}