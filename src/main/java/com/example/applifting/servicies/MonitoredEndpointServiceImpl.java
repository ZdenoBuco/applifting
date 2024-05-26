package com.example.applifting.servicies;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.AppUser;
import com.example.applifting.models.InDTOs.MonitoredEndpointInDTO;
import com.example.applifting.models.MonitoredEndpoint;
import com.example.applifting.models.MonitoringResult;
import com.example.applifting.models.OutDTOs.MonitoredEndpointOutDTO;
import com.example.applifting.models.OutDTOs.MonitoringResultOutDTO;
import com.example.applifting.repositories.MonitoredEndpointRepository;
import com.example.applifting.repositories.MonitoringResultRepository;
import com.example.applifting.utilities.MonitoredEndpointInDTOValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MonitoredEndpointServiceImpl implements MonitoredEndpointService {
    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final MonitoringResultRepository monitoringResultRepository;
    private final DynamicMonitoringService dynamicMonitoringService;

    private UUID authenticatedUserId;


    @Override
    public MonitoredEndpointOutDTO getEndpoint(UUID monitoredEndpointId, Integer resultLimit) {
        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(monitoredEndpointId).orElseThrow(() -> new AppliftingException("Monitored Endpoint not found", 404));
        checkIfAuthenticatedUserIsAllowedForEndpoint(endpoint);
        return MonitoredEndpointOutDTO.builder()
                .id(endpoint.getId())
                .name(endpoint.getName())
                .url(endpoint.getUrl())
                .createdAt(endpoint.getCreatedAt())
                .lastCheck(endpoint.getLastCheck())
                .monitoringInterval(endpoint.getMonitoringInterval())
                .monitoringResults(getResultOutDTOList(endpoint, resultLimit))
                .ownerId(endpoint.getOwnerId())
                .build();
    }

    @Override
    public List<MonitoredEndpointOutDTO> getEndpoints(Integer resultLimit) {
        setAuthenticatedUser();
        return monitoredEndpointRepository.findMonitoredEndpointByOwnerId(authenticatedUserId).stream().map(endpoint -> MonitoredEndpointOutDTO.builder()
                        .id(endpoint.getId())
                        .name(endpoint.getName())
                        .url(endpoint.getUrl())
                        .createdAt(endpoint.getCreatedAt())
                        .lastCheck(endpoint.getLastCheck())
                        .monitoringInterval(endpoint.getMonitoringInterval())
                        .monitoringResults(getResultOutDTOList(endpoint, resultLimit))
                        .ownerId(endpoint.getOwnerId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public MonitoredEndpointOutDTO createEndpoint(MonitoredEndpointInDTO monitoredEndpointInDTO) {
        MonitoredEndpointInDTOValidator.validate(monitoredEndpointInDTO);

        setAuthenticatedUser();

        MonitoredEndpoint endpoint = MonitoredEndpoint.builder()
                .name(monitoredEndpointInDTO.getName())
                .url(monitoredEndpointInDTO.getUrl())
                .createdAt(LocalDateTime.now())
                .monitoringInterval(monitoredEndpointInDTO.getMonitoringInterval())
                .ownerId(authenticatedUserId)
                .build();

        return processMonitoredEndpoint(endpoint);
    }

    @Override
    public MonitoredEndpointOutDTO updateEndpoint(MonitoredEndpointInDTO monitoredEndpointInDTO, UUID monitoredEndpointId) {
        MonitoredEndpointInDTOValidator.validate(monitoredEndpointInDTO);

        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(monitoredEndpointId)
                .orElseThrow(() -> new AppliftingException("Monitored Endpoint not found", 404));
        checkIfAuthenticatedUserIsAllowedForEndpoint(endpoint);

        endpoint.setName(monitoredEndpointInDTO.getName());
        endpoint.setMonitoringInterval(monitoredEndpointInDTO.getMonitoringInterval());

        return processMonitoredEndpoint(endpoint);
    }

    @Override
    public MonitoredEndpointOutDTO deleteEndpoint(UUID monitoredEndpointId) {

        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(monitoredEndpointId)
                .orElseThrow(() -> new AppliftingException("Monitored Endpoint not found", 404));
        checkIfAuthenticatedUserIsAllowedForEndpoint(endpoint);
        monitoredEndpointRepository.delete(endpoint);

        dynamicMonitoringService.removeMonitoringTask(monitoredEndpointId);

        return MonitoredEndpointOutDTO.builder()
                .id(endpoint.getId())
                .name(endpoint.getName())
                .url(endpoint.getUrl())
                .createdAt(endpoint.getCreatedAt())
                .lastCheck(endpoint.getLastCheck())
                .monitoringInterval(endpoint.getMonitoringInterval())
                .ownerId(endpoint.getOwnerId())
                .build();
    }

    private List<MonitoringResultOutDTO> getResultOutDTOList(MonitoredEndpoint endpoint, Integer resultLimit) {
        Stream<MonitoringResult> resultStream = monitoringResultRepository
                .findAllByMonitoredEndpointIdOrderByCheckedAtDesc(endpoint.getId())
                .stream();

        if (resultLimit != null) {
            resultStream = resultStream.limit(resultLimit);
        }

        return resultStream.map(result -> MonitoringResultOutDTO.builder()
                        .id(result.getId())
                        .checkedAt(result.getCheckedAt())
                        .httpCode(result.getHttpCode())
                        .payload(result.getPayload())
                        .build())
                .collect(Collectors.toList());
    }

    private MonitoredEndpointOutDTO processMonitoredEndpoint(MonitoredEndpoint endpoint) {
        MonitoredEndpoint updatedEndpoint = monitoredEndpointRepository.save(endpoint);

        MonitoredEndpointOutDTO createdEndpointOutDTO = MonitoredEndpointOutDTO.builder()
                .id(updatedEndpoint.getId())
                .name(updatedEndpoint.getName())
                .url(updatedEndpoint.getUrl())
                .createdAt(updatedEndpoint.getCreatedAt())
                .lastCheck(updatedEndpoint.getLastCheck())
                .monitoringInterval(updatedEndpoint.getMonitoringInterval())
                .ownerId(updatedEndpoint.getOwnerId())
                .build();

        dynamicMonitoringService.updateMonitoringTask(createdEndpointOutDTO);
        return createdEndpointOutDTO;
    }

    private void setAuthenticatedUser() {
        Optional<AppUser> user = (Optional<AppUser>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authenticatedUserId = user.map(AppUser::getId).orElse(null);
    }

    private void checkIfAuthenticatedUserIsAllowedForEndpoint(MonitoredEndpoint endpoint) {
        setAuthenticatedUser();
        if (!authenticatedUserId.equals(endpoint.getOwnerId())) {
            throw new AppliftingException("User is not allowed to access endpoint", 403);
        }
    }
}