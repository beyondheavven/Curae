package com.vention.curae.appointment.snapshot.repository;

import com.vention.curae.appointment.snapshot.model.ServiceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceSnapshotRepository extends JpaRepository<ServiceSnapshot, Long> {
}