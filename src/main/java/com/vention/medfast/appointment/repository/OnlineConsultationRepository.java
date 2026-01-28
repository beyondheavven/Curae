package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.model.OnlineConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlineConsultationRepository extends JpaRepository<OnlineConsultation, Long> {

}
