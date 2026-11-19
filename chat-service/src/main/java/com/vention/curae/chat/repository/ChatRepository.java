package com.vention.curae.chat.repository;

import com.vention.curae.chat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

	Optional<Chat> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

	List<Chat> findAllByPatientIdOrDoctorId(Long patientId, Long doctorId);

	boolean existsByPatientIdAndDoctorId(Long patientId, Long doctorId);

}
