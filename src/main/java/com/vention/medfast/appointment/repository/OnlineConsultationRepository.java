package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.model.OnlineConsultation;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlineConsultationRepository extends JpaRepository<OnlineConsultation, Long> {

	@Query("""
			select new com.vention.medfast.appointment.dto.OnlineConsultationDetails(
			    a.id,
			    p.id,
			    d.id,
			    a.date,
			    a.time,
			    a.status,
			    a.type,
			    a.updatedAt,
			    oc.meetingLink
			)
			from OnlineConsultation oc
			join Appointment a on a.id = oc.id
			join oc.patient p
			join oc.doctor d
			where oc.id = :id
			""")
	Optional<OnlineConsultationDetails> findDetailsById(@Param("id") Long id);

	@Query("""
			SELECT new com.vention.medfast.statistics.dto.response.AppointmentStatusCounts(
			    COUNT(oc),
			    COALESCE(SUM(CASE
			        WHEN oc.status IN ('SCHEDULED','SCHEDULED_CONFIRMED','IN_CONSULTATION')
			        THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN oc.status = 'CANCELLED' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN oc.status = 'COMPLETED' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN oc.status = 'MISSED' THEN 1 ELSE 0 END), 0)
			)
			FROM OnlineConsultation oc
			JOIN oc.doctor d
			JOIN d.clinics c
			WHERE c.id = :clinicId
			  AND oc.date BETWEEN :startDate AND :endDate
			""")
	AppointmentStatusCounts getStatusStatisticsByClinic(@Param("clinicId") Long clinicId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("""
			SELECT new com.vention.medfast.statistics.dto.response.AppointmentStatusCounts(
			    COUNT(oc),
			    COALESCE(SUM(CASE
			        WHEN oc.status IN ('SCHEDULED','SCHEDULED_CONFIRMED','IN_CONSULTATION')
			        THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN oc.status = 'CANCELLED' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN oc.status = 'COMPLETED' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN oc.status = 'MISSED' THEN 1 ELSE 0 END), 0)
			)
			FROM OnlineConsultation oc
			WHERE oc.doctor.id = :doctorId
			  AND oc.date BETWEEN :startDate AND :endDate
			""")
	AppointmentStatusCounts getStatusStatisticsByDoctor(@Param("doctorId") Long doctorId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
