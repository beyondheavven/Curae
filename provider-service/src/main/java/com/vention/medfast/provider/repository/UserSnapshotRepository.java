package com.vention.medfast.provider.repository;

import com.vention.medfast.provider.model.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, Long> {

}
