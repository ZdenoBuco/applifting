package com.example.applifting.repositories;

import com.example.applifting.models.MonitoredEndpoint;
import com.example.applifting.models.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, UUID> {
    List<MonitoringResult> findAllByMonitoredEndpointOrderByCheckedAtDesc(MonitoredEndpoint monitoredEndpoint);
}
