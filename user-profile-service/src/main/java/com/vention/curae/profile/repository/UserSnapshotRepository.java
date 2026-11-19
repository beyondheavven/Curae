package com.vention.curae.profile.repository;

import com.vention.curae.profile.model.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, Long> {

}
