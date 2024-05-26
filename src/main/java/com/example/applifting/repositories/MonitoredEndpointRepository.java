package com.example.applifting.repositories;

import com.example.applifting.models.MonitoredEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, UUID> {
    List<MonitoredEndpoint> findMonitoredEndpointByOwnerId(UUID appUserId);
}
