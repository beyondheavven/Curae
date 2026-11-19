package com.vention.curae.appointment.repository;

import com.vention.curae.appointment.model.OnlineConsultation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnlineConsultationRepository extends JpaRepository<OnlineConsultation, Long> {

	Optional<OnlineConsultation> findByAppointmentId(Long appointmentId);

}
