package com.example.applifting.models.InDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoredEndpointInDTO {
    private String name;
    private String url;
    private Integer monitoringInterval;
}
