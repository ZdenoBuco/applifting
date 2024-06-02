package com.example.applifting.servicies;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.InDTOs.MonitoredEndpointInDTO;
import com.example.applifting.models.MonitoredEndpoint;
import com.example.applifting.models.OutDTOs.MonitoredEndpointOutDTO;
import com.example.applifting.repositories.MonitoredEndpointRepository;
import com.example.applifting.repositories.MonitoringResultRepository;
import com.example.applifting.testData.MonitoredEndpointServiceTD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MonitoredEndpointServiceImplTest {
    @InjectMocks
    private TestableMonitoredEndpointServiceImpl underTest;
    @Mock
    private MonitoredEndpointRepository monitoredEndpointRepository;
    @Mock
    private MonitoringResultRepository monitoringResultRepository;
    @Mock
    private DynamicMonitoringService dynamicMonitoringService;
    private static final UUID ENDPOINT_ID = UUID.randomUUID();
    private static final String ENDPOINT_NAME = "endpointName";
    private static final String ENDPOINT_URL = "www.endpointUrl.url";
    private static final String ENDPOINT_NAME_2 = "endpointName2";
    private static final String ENDPOINT_URL_2 = "www.endpointUrl2.url";
    private static final LocalDateTime ENDPOINT_CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime ENDPOINT_LAST_CHECK = ENDPOINT_CREATED_AT.plusSeconds(10);
    private static final Integer ENDPOINT_INTERVAL = 10;
    private static final UUID ENDPOINT_OWNER_ID = UUID.randomUUID();
    private static final UUID RESULT_ID = UUID.randomUUID();
    private MonitoredEndpoint monitoredEndpoint;
    private MonitoredEndpointOutDTO expectedEndpointOutDTO;
    private MonitoredEndpointInDTO endpointInDTO;

    static class TestableMonitoredEndpointServiceImpl extends MonitoredEndpointServiceImpl {
        protected UUID authenticatedUserId = ENDPOINT_OWNER_ID;

        public TestableMonitoredEndpointServiceImpl(MonitoredEndpointRepository monitoredEndpointRepository,
                                                    MonitoringResultRepository monitoringResultRepository,
                                                    DynamicMonitoringService dynamicMonitoringService) {
            super(monitoredEndpointRepository, monitoringResultRepository, dynamicMonitoringService);
        }

        @Override
        protected UUID getAuthenticatedUserId() {
            return authenticatedUserId;
        }
    }

    @BeforeEach
    void setUp() {
        endpointInDTO = buildMonitoredEndpointInDTOTemplate();
        monitoredEndpoint = buildExpectedEndpointTemplate();
        expectedEndpointOutDTO = buildExpectedEndpointOutDTOTemplate();
        lenient().when(monitoredEndpointRepository.findById(ENDPOINT_ID)).thenReturn(Optional.of(monitoredEndpoint));
        lenient().when(monitoringResultRepository.findAllByMonitoredEndpointIdOrderByCheckedAtDesc(ENDPOINT_ID)).thenReturn(MonitoredEndpointServiceTD.getMonitoringResults(RESULT_ID, ENDPOINT_ID));
        lenient().when(monitoredEndpointRepository.findMonitoredEndpointsByOwnerId(ENDPOINT_OWNER_ID)).thenReturn(List.of(monitoredEndpoint));
    }

    @Test
    void getEndpoint_everythingIsOk_createsCorrectMonitoredEndpointOutDTO() {
        MonitoredEndpointOutDTO actualMonitoredEndpointOutDTO = underTest.getEndpoint(ENDPOINT_ID, null);

        assertEquals(expectedEndpointOutDTO, actualMonitoredEndpointOutDTO);
    }

    @Test
    void getEndpoint_endpointDoesntExist_throwsException() {
        when(monitoredEndpointRepository.findById(ENDPOINT_ID)).thenReturn(Optional.empty());

        AppliftingException exception = assertThrows(AppliftingException.class, () -> underTest.getEndpoint(ENDPOINT_ID, null));
        assertEquals(exception.getMessage(), "Monitored Endpoint not found");
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void getEndpoints_everythingIsOk_createsCorrectMonitoredEndpointOutDTO() {
        List<MonitoredEndpointOutDTO> actualMonitoredEndpointOutDTOList = underTest.getEndpoints(null);

        assertEquals(List.of(expectedEndpointOutDTO), actualMonitoredEndpointOutDTOList);
    }

    @Test
    void getEndpoints_noEndpoints_returnsEmptyList() {
        when(monitoredEndpointRepository.findMonitoredEndpointsByOwnerId(ENDPOINT_OWNER_ID)).thenReturn(new ArrayList<>());

        List<MonitoredEndpointOutDTO> actualMonitoredEndpointOutDTOList = underTest.getEndpoints(null);

        assertEquals(new ArrayList<>(), actualMonitoredEndpointOutDTOList);
    }

    @Test
    void createEndpoint_everythingIsOk_createsCorrectMonitoredEndpointOutDTO() {
        monitoredEndpoint.setMonitoringResults(null);
        expectedEndpointOutDTO.setMonitoringResults(null);
        when(monitoredEndpointRepository.save(any())).thenReturn(monitoredEndpoint);

        MonitoredEndpointOutDTO actualEndpointOutDTO = underTest.createEndpoint(endpointInDTO);

        assertEquals(expectedEndpointOutDTO, actualEndpointOutDTO);
        verify(monitoredEndpointRepository).save(any());
        verify(dynamicMonitoringService).updateMonitoringTask(any());
    }

    @Test
    void updateEndpoint_everythingIsOk_updatesMonitoredEndpoint() {
        endpointInDTO.setName(ENDPOINT_NAME_2);
        endpointInDTO.setUrl(ENDPOINT_URL_2);
        when(monitoredEndpointRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        MonitoredEndpointOutDTO actualEndpointOutDTO = underTest.updateEndpoint(endpointInDTO, ENDPOINT_ID);
//        shouldn't set email
        expectedEndpointOutDTO.setName(ENDPOINT_NAME_2);
        expectedEndpointOutDTO.setMonitoringResults(null);

        assertEquals(expectedEndpointOutDTO, actualEndpointOutDTO);
        verify(dynamicMonitoringService).updateMonitoringTask(any());
    }

    @Test
    void updateEndpoint_endpointIdDoesNotExist_throwsException() {
        AppliftingException exception = assertThrows(AppliftingException.class, () -> underTest.updateEndpoint(endpointInDTO, UUID.randomUUID()));

        assertEquals(exception.getMessage(), "Monitored Endpoint not found");
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void deleteEndpoint_everythingIsOk_deletesMonitoredEndpoint() {
        expectedEndpointOutDTO.setMonitoringResults(null);

        MonitoredEndpointOutDTO actualEndpointOutDTO = underTest.deleteEndpoint(ENDPOINT_ID);

        assertEquals(expectedEndpointOutDTO, actualEndpointOutDTO);
        verify(dynamicMonitoringService).removeMonitoringTask(any());
        verify(monitoredEndpointRepository).delete(any());
    }

    @Test
    void deleteEndpoint_endpointIdDoesNotExist_throwsException() {
        AppliftingException exception = assertThrows(AppliftingException.class, () -> underTest.deleteEndpoint(UUID.randomUUID()));

        assertEquals(exception.getMessage(), "Monitored Endpoint not found");
        assertEquals(404, exception.getStatusCode());
    }

//    @Test
//    void getResultOutDTOList_limitIsNull_createsCorrectResultOutDTOLIst() {
////        tested in getEndpoint_everythingIsOk_createsCorrectMonitoredEndpointOutDTO()
//        MonitoredEndpointOutDTO actualMonitoredEndpointOutDTO = underTest.getEndpoint(ENDPOINT_ID, null);
//
//        assertEquals(expectedEndpointOutDTO, actualMonitoredEndpointOutDTO);
//    }

    @Test
    void getResultOutDTOList_limitIsBiggerThanListSize_createsCorrectResultOutDTOLIst() {
        MonitoredEndpointOutDTO actualMonitoredEndpointOutDTO = underTest.getEndpoint(ENDPOINT_ID, 1000);

        assertEquals(expectedEndpointOutDTO, actualMonitoredEndpointOutDTO);
    }

    @Test
    void getResultOutDTOList_limitIsSmallerThanListSize_createsCorrectResultOutDTOLIst() {
        MonitoredEndpointOutDTO actualMonitoredEndpointOutDTO = underTest.getEndpoint(ENDPOINT_ID, 3);
        expectedEndpointOutDTO.setMonitoringResults(expectedEndpointOutDTO.getMonitoringResults().subList(0, 3));

        assertEquals(expectedEndpointOutDTO, actualMonitoredEndpointOutDTO);
    }

    // processMonitoredEndpoint() tested in previous tests

//    @Test
//    void checkIfAuthenticatedUserIsAllowedForEndpoint_correctUserAuthenticated_dontThrowsException() {
////        tested in getEndpoint_everythingIsOk_createsCorrectMonitoredEndpointOutDTO()
//
//        MonitoredEndpointOutDTO actualMonitoredEndpointOutDTO = underTest.getEndpoint(ENDPOINT_ID, null);
//
//        assertEquals(expectedEndpointOutDTO, actualMonitoredEndpointOutDTO);
//    }

    @Test
    void checkIfAuthenticatedUserIsAllowedForEndpoint_differentUserAuthenticated_throwsException() {
        underTest.authenticatedUserId = UUID.randomUUID();

        AppliftingException exception = assertThrows(AppliftingException.class, () -> underTest.getEndpoint(ENDPOINT_ID, null));
        assertEquals(exception.getMessage(), "User is not allowed to access endpoint");
        assertEquals(403, exception.getStatusCode());
    }

    private MonitoredEndpointOutDTO buildExpectedEndpointOutDTOTemplate() {
        return MonitoredEndpointOutDTO.builder()
                .id(ENDPOINT_ID)
                .name(ENDPOINT_NAME)
                .url(ENDPOINT_URL)
                .createdAt(ENDPOINT_CREATED_AT)
                .lastCheck(ENDPOINT_LAST_CHECK)
                .monitoringInterval(ENDPOINT_INTERVAL)
                .ownerId(ENDPOINT_OWNER_ID)
                .monitoringResults(MonitoredEndpointServiceTD.getMonitoringResultsOutDTO(RESULT_ID, ENDPOINT_ID))
                .build();
    }

    private MonitoredEndpoint buildExpectedEndpointTemplate() {
        return MonitoredEndpoint.builder()
                .id(ENDPOINT_ID)
                .name(ENDPOINT_NAME)
                .url(ENDPOINT_URL)
                .createdAt(ENDPOINT_CREATED_AT)
                .lastCheck(ENDPOINT_LAST_CHECK)
                .monitoringInterval(ENDPOINT_INTERVAL)
                .ownerId(ENDPOINT_OWNER_ID)
                .monitoringResults(MonitoredEndpointServiceTD.getMonitoringResults(RESULT_ID, ENDPOINT_ID))
                .build();
    }

    private MonitoredEndpointInDTO buildMonitoredEndpointInDTOTemplate() {
        return MonitoredEndpointInDTO.builder()
                .name(ENDPOINT_NAME)
                .url(ENDPOINT_URL)
                .monitoringInterval(ENDPOINT_INTERVAL)
                .build();
    }
}