package com.example.applifting.servicies;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.InDTOs.MonitoredEndpointInDTO;
import com.example.applifting.models.MonitoredEndpoint;
import com.example.applifting.models.MonitoringResult;
import com.example.applifting.models.OutDTOs.MonitoredEndpointOutDTO;
import com.example.applifting.models.OutDTOs.MonitoringResultOutDTO;
import com.example.applifting.repositories.AppUserRepository;
import com.example.applifting.repositories.MonitoredEndpointRepository;
import com.example.applifting.repositories.MonitoringResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MonitoredEndpointServiceImpl implements MonitoredEndpointService {
    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final AppUserRepository appUserRepository;
    private final MonitoringResultRepository monitoringResultRepository;

//    TODO: implement security

    @Override
    public MonitoredEndpointOutDTO getEndpoint(UUID monitoredEndpointId, Integer resultLimit) {
        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(monitoredEndpointId).orElseThrow(() -> new AppliftingException("Monitored Endpoint not found", 404));
        return MonitoredEndpointOutDTO.builder()
                .id(endpoint.getId())
                .name(endpoint.getName())
                .url(endpoint.getUrl())
                .createdAt(endpoint.getCreatedAt())
                .lastCheck(endpoint.getLastCheck())
                .monitoringInterval(endpoint.getMonitoringInterval())
                .monitoringResults(getResultOutDTOList(endpoint, resultLimit))
                .ownerId(endpoint.getOwner().getId())
                .build();
    }

    @Override
    public List<MonitoredEndpointOutDTO> getEndpoints(Integer resultLimit) {
        return monitoredEndpointRepository.findAll().stream().map(endpoint -> MonitoredEndpointOutDTO.builder()
                        .id(endpoint.getId())
                        .name(endpoint.getName())
                        .url(endpoint.getUrl())
                        .createdAt(endpoint.getCreatedAt())
                        .lastCheck(endpoint.getLastCheck())
                        .monitoringInterval(endpoint.getMonitoringInterval())
                        .monitoringResults(getResultOutDTOList(endpoint, resultLimit))
                        .ownerId(endpoint.getOwner().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public MonitoredEndpointOutDTO createEndpoint(MonitoredEndpointInDTO monitoredEndpointInDTO) {
        MonitoredEndpoint endpoint = MonitoredEndpoint.builder()
                .name(monitoredEndpointInDTO.getName())
                .url(monitoredEndpointInDTO.getUrl())
                .createdAt(LocalDateTime.now())
                .monitoringInterval(monitoredEndpointInDTO.getMonitoringInterval())
// TODO: dosadit prihlaseneho usera                .owner(appUserRepository.getById(appUserId).getId())
                .build();

        MonitoredEndpoint savedEndpoint = monitoredEndpointRepository.save(endpoint);

        return MonitoredEndpointOutDTO.builder()
                .id(savedEndpoint.getId())
                .name(savedEndpoint.getName())
                .url(savedEndpoint.getUrl())
                .createdAt(savedEndpoint.getCreatedAt())
                .lastCheck(savedEndpoint.getLastCheck())
                .monitoringInterval(savedEndpoint.getMonitoringInterval())
// TODO               .ownerId(savedEndpoint.getOwner().getId())
                .build();
    }

    @Override
    public MonitoredEndpointOutDTO updateEndpoint(MonitoredEndpointInDTO monitoredEndpointInDTO, UUID monitoredEndpointId) {
//        TODO: security check - patri endoint na zmenu prihlasenemu userovi?
        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(monitoredEndpointId)
                .orElseThrow(() -> new AppliftingException("Monitored Endpoint not found", 404));

        endpoint.setName(monitoredEndpointInDTO.getName());
        endpoint.setMonitoringInterval(monitoredEndpointInDTO.getMonitoringInterval());

        MonitoredEndpoint updatedEndpoint = monitoredEndpointRepository.save(endpoint);

        return MonitoredEndpointOutDTO.builder()
                .id(updatedEndpoint.getId())
                .name(updatedEndpoint.getName())
                .url(updatedEndpoint.getUrl())
                .createdAt(updatedEndpoint.getCreatedAt())
                .lastCheck(updatedEndpoint.getLastCheck())
                .monitoringInterval(updatedEndpoint.getMonitoringInterval())
                .ownerId(updatedEndpoint.getOwner().getId())
                .build();
    }

    @Override
    public MonitoredEndpointOutDTO deleteEndpoint(UUID monitoredEndpointId) {
        //        TODO: security check - patri endoint na zmazanie prihlasenemu userovi?
        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(monitoredEndpointId)
                .orElseThrow(() -> new AppliftingException("Monitored Endpoint not found", 404));

        monitoredEndpointRepository.delete(endpoint);

        return MonitoredEndpointOutDTO.builder()
                .id(endpoint.getId())
                .name(endpoint.getName())
                .url(endpoint.getUrl())
                .createdAt(endpoint.getCreatedAt())
                .lastCheck(endpoint.getLastCheck())
                .monitoringInterval(endpoint.getMonitoringInterval())
                .ownerId(endpoint.getOwner().getId())
                .build();
    }

    private List<MonitoringResultOutDTO> getResultOutDTOList(MonitoredEndpoint endpoint, Integer resultLimit) {
        Stream<MonitoringResult> resultStream = monitoringResultRepository
                .findAllByMonitoredEndpointOrderByCheckedAtDesc(endpoint)
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
}