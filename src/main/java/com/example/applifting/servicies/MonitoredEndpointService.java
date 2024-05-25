package com.example.applifting.servicies;

import com.example.applifting.models.InDTOs.MonitoredEndpointInDTO;
import com.example.applifting.models.OutDTOs.MonitoredEndpointOutDTO;

import java.util.List;
import java.util.UUID;

public interface MonitoredEndpointService {
    MonitoredEndpointOutDTO getEndpoint(UUID monitoredEndpointId, Integer resultLimit);
    List<MonitoredEndpointOutDTO> getEndpoints(Integer resultLimit);
    MonitoredEndpointOutDTO createEndpoint(MonitoredEndpointInDTO monitoredEndpointInDTO);
    MonitoredEndpointOutDTO updateEndpoint(MonitoredEndpointInDTO monitoredEndpointInDTO, UUID monitoredEndpointId);
    MonitoredEndpointOutDTO deleteEndpoint(UUID monitoredEndpointId);

}
