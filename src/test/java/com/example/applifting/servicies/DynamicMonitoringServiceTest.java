package com.example.applifting.servicies;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.MonitoredEndpoint;
import com.example.applifting.models.MonitoringResult;
import com.example.applifting.models.OutDTOs.MonitoredEndpointOutDTO;
import com.example.applifting.repositories.MonitoredEndpointRepository;
import com.example.applifting.repositories.MonitoringResultRepository;
import com.example.applifting.testData.MonitoredEndpointServiceTD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicMonitoringServiceTest {
    @InjectMocks
    private DynamicMonitoringService underTest;

    @Mock
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Mock
    private MonitoringResultRepository monitoringResultRepository;

    List<MonitoredEndpoint> monitoredEndpointList;

    @BeforeEach
    void setUp() {
        monitoredEndpointList = MonitoredEndpointServiceTD.getMonitoredEndpoints(5);
        lenient().when(monitoredEndpointRepository.findAll()).thenReturn(monitoredEndpointList);

    }

    @Test
    void init_fiveEndpointInDB_schedulesFiveTasks() {
        underTest.init();

        verify(monitoredEndpointRepository, times(1)).findAll();
        assertEquals(5, underTest.getTasks().size());
    }

    @Test
    void updateMonitoringTask_everythingIsOk_updatesTask() {
        MonitoredEndpoint endpointToUpdate = monitoredEndpointList.getFirst();
        MonitoredEndpointOutDTO updatedEndpoint = MonitoredEndpointOutDTO.builder()
                .id(endpointToUpdate.getId())
                .name("new awesome name")
                .url(endpointToUpdate.getUrl())
                .createdAt(endpointToUpdate.getCreatedAt())
                .lastCheck(endpointToUpdate.getLastCheck())
                .monitoringInterval(endpointToUpdate.getMonitoringInterval())
                .monitoringResults(null)
                .ownerId(endpointToUpdate.getOwnerId())
                .build();

        when(monitoredEndpointRepository.findById(endpointToUpdate.getId())).thenReturn(Optional.of(endpointToUpdate));

        underTest.init();
        ScheduledFuture<?> taskBeforeUpdate = underTest.getTasks().get(endpointToUpdate.getId());
        underTest.updateMonitoringTask(updatedEndpoint);

        ScheduledFuture<?> taskAfterUpdate = underTest.getTasks().get(endpointToUpdate.getId());

        assertNotNull(taskBeforeUpdate);
        assertNotNull(taskAfterUpdate);
        assertNotEquals(taskBeforeUpdate, taskAfterUpdate);
    }

    @Test
    void updateMonitoringTask_taskIdDoesntExist_throwsException() {
        MonitoredEndpoint endpointToUpdate = monitoredEndpointList.getFirst();
        MonitoredEndpointOutDTO updatedEndpoint = MonitoredEndpointOutDTO.builder()
                .id(UUID.randomUUID())
                .name("new awesome name")
                .url(endpointToUpdate.getUrl())
                .createdAt(endpointToUpdate.getCreatedAt())
                .lastCheck(endpointToUpdate.getLastCheck())
                .monitoringInterval(endpointToUpdate.getMonitoringInterval())
                .monitoringResults(null)
                .ownerId(endpointToUpdate.getOwnerId())
                .build();

        when(monitoredEndpointRepository.findById(any())).thenReturn(Optional.empty());

        AppliftingException exception = assertThrows(AppliftingException.class, () -> underTest.updateMonitoringTask(updatedEndpoint));
        assertEquals(404, exception.getStatusCode());
        assertEquals("Endpoint with ID " + updatedEndpoint.getId() + " does not exist.", exception.getMessage());
    }

    @Test
    void removeMonitoringTask_everythingIsOk_removesTask() {
        MonitoredEndpoint endpointToDelete = monitoredEndpointList.getFirst();
        underTest.init();
        underTest.removeMonitoringTask(endpointToDelete.getId());

        assertEquals(4, underTest.getTasks().size());
        assertFalse(underTest.getTasks().containsKey(endpointToDelete.getId()));
    }

    @Test
    void removeMonitoringTask_IDToDeleteDoesntExists_ignoreRequest() {
        underTest.init();
        underTest.removeMonitoringTask(UUID.randomUUID());

        assertEquals(5, underTest.getTasks().size());
    }

    @Test
    void checkEndpoint_createsMonitoringResult_onSuccess() {
        ArgumentCaptor<MonitoredEndpoint> captorEndpoint = ArgumentCaptor.forClass(MonitoredEndpoint.class);
        ArgumentCaptor<MonitoringResult> captorResult = ArgumentCaptor.forClass(MonitoringResult.class);
        MonitoredEndpoint endpoint = MonitoredEndpointServiceTD.getMonitoredEndpoints(1).getFirst();
        endpoint.setMonitoringInterval(1);
        endpoint.setUrl("https://www.google.com");
        when(monitoredEndpointRepository.findAll()).thenReturn(List.of(endpoint));

        underTest.init();

        verify(monitoredEndpointRepository, timeout(1500)).save(captorEndpoint.capture());
        verify(monitoringResultRepository, timeout(1500)).save(captorResult.capture());
        assertEquals(200, captorResult.getValue().getHttpCode());
        assertTrue(captorEndpoint.getValue().getLastCheck().isAfter(captorEndpoint.getValue().getCreatedAt()));
    }


    @Test
    void checkEndpoint_createsMonitoringResult_onFailure() {
        ArgumentCaptor<MonitoredEndpoint> captorEndpoint = ArgumentCaptor.forClass(MonitoredEndpoint.class);
        ArgumentCaptor<MonitoringResult> captorResult = ArgumentCaptor.forClass(MonitoringResult.class);
        MonitoredEndpoint endpoint = MonitoredEndpointServiceTD.getMonitoredEndpoints(1).getFirst();
        endpoint.setMonitoringInterval(1);
        when(monitoredEndpointRepository.findAll()).thenReturn(List.of(endpoint));

        underTest.init();

        verify(monitoredEndpointRepository, timeout(1500)).save(captorEndpoint.capture());
        verify(monitoringResultRepository, timeout(1500)).save(captorResult.capture());
        assertEquals(500, captorResult.getValue().getHttpCode());
        assertTrue(captorEndpoint.getValue().getLastCheck().isAfter(captorEndpoint.getValue().getCreatedAt()));
    }
}