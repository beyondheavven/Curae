package com.vention.medfast.profile.repository;

import com.vention.medfast.profile.model.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, Long> {

}
