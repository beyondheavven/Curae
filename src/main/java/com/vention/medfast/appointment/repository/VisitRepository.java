package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.Visit;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

	@Query("""
			select new com.vention.medfast.appointment.dto.VisitListItemDto(
			    du.firstName,
			    du.lastName,
			    c.name,
			    addr.city,
			    a.date,
			    a.time,
			    a.status
			)
			from Appointment a
			join Visit v on v.id = a.id
			join v.doctor d
			join d.user du
			join v.clinic c
			join c.address addr
			where a.patient.id = :patientId
			  and a.type = com.vention.medfast.appointment.model.AppointmentType.VISIT
			  and (a.date, a.time) > (:today, :now)
			order by a.date, a.time
			""")
	Slice<VisitListItemDto> findUpcomingByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			@Param("now") LocalTime now, Pageable pageable);

	@Query("""
			select new com.vention.medfast.appointment.dto.VisitListItemDto(
			    du.firstName,
			    du.lastName,
			    c.name,
			    addr.city,
			    a.date,
			    a.time,
			    a.status
			)
			from Appointment a
			join Visit v on v.id = a.id
			join v.doctor d
			join d.user du
			join v.clinic c
			join c.address addr
			where a.patient.id = :patientId
			  and a.type = com.vention.medfast.appointment.model.AppointmentType.VISIT
			  and (a.date, a.time) <= (:today, :now)
			order by a.date desc, a.time desc
			""")
	Slice<VisitListItemDto> findPastByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			@Param("now") LocalTime now, Pageable pageable);

	@Query("""
			SELECT new com.vention.medfast.statistics.dto.response.AppointmentStatusCounts(
			    COUNT(v),
			    COALESCE(SUM(CASE
			        WHEN v.status IN ('SCHEDULED','SCHEDULED_CONFIRMED','IN_CONSULTATION')
			        THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN v.status = 'CANCELLED' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN v.status = 'COMPLETED' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN v.status = 'MISSED' THEN 1 ELSE 0 END), 0)
			)
			FROM Visit v
			WHERE v.clinic.id = :clinicId
			  AND v.date BETWEEN :startDate AND :endDate
			""")
	AppointmentStatusCounts getStatusStatisticsByClinic(@Param("clinicId") Long clinicId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("""
			SELECT new com.vention.medfast.statistics.dto.response.AppointmentStatusCounts(
			    COUNT(v),
			    COALESCE(SUM(CASE
			        WHEN v.status IN ('SCHEDULED','SCHEDULED_CONFIRMED','IN_CONSULTATION')
			        THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN v.status = 'CANCELLED' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN v.status = 'COMPLETED' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN v.status = 'MISSED' THEN 1 ELSE 0 END), 0)
			)
			FROM Visit v
			WHERE v.doctor.id = :doctorId
			  AND v.date BETWEEN :startDate AND :endDate
			""")
	AppointmentStatusCounts getStatusStatisticsByDoctor(@Param("doctorId") Long doctorId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
