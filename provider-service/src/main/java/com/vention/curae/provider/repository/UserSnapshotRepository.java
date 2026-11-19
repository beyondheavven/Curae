package com.vention.curae.provider.repository;

import com.vention.curae.provider.model.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, Long> {

}
