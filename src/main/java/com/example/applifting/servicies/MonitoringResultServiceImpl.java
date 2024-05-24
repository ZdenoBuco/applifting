package com.example.applifting.servicies;

import com.example.applifting.repositories.MonitoringResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitoringResultServiceImpl implements MonitoringResultService {
    private final MonitoringResultRepository monitoringResultRepository;
}
