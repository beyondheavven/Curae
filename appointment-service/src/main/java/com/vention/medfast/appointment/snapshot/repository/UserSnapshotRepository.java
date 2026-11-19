package com.vention.medfast.appointment.snapshot.repository;

import com.vention.medfast.appointment.snapshot.model.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, Long> {
}