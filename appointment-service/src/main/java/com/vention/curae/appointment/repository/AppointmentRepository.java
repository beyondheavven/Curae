package com.vention.curae.appointment.repository;

import com.vention.curae.appointment.model.Appointment;
import com.vention.curae.appointment.model.AppointmentStatus;
import com.vention.curae.appointment.model.AppointmentType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	Page<Appointment> findByPatientIdAndTypeAndStatusInOrderByScheduledDateDescScheduledTimeDesc(Long patientId,
			AppointmentType type, Collection<AppointmentStatus> statuses, Pageable pageable);

	List<Appointment> findByPatientIdAndTypeAndStatusIn(Long patientId, AppointmentType type,
			Collection<AppointmentStatus> statuses);

	@Query("select count(a) > 0 from Appointment a, Referral r where a.referralId = r.id and a.patientId = :patientId "
			+ "and r.issuedBy = :doctorId and a.status = :status")
	boolean existsByPatientIdAndDoctorIdAndStatus(@Param("patientId") Long patientId, @Param("doctorId") Long doctorId,
			@Param("status") AppointmentStatus status);

	@Query("select a.status, count(a) from Appointment a group by a.status")
	List<Object[]> countByStatus();

}
