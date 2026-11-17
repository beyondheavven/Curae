package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.model.OnlineConsultation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnlineConsultationRepository extends JpaRepository<OnlineConsultation, Long> {

	Optional<OnlineConsultation> findByAppointmentId(Long appointmentId);

}
