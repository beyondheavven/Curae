package com.vention.curae.appointment.snapshot.repository;

import com.vention.curae.appointment.snapshot.model.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, Long> {
}