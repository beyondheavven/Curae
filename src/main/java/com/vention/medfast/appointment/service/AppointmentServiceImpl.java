package com.vention.medfast.appointment.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.FirstAppointmentCreateDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.mapper.AppointmentMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.service.ReferralService;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.labresult.mapper.LabResultMapper;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.service.LabTestService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;

	private final AppointmentMapper appointmentMapper;

	private final PatientService patientService;

	private final DoctorService doctorService;

	private final ReferralService referralService;

	private final LabTestService labTestService;

	private final LabResultService labResultService;

	private final LabResultMapper labResultMapper;

	@Override
	@Transactional
	public Long createFirst(FirstAppointmentCreateDto dto) {
		Patient patient = patientService.getCurrentPatient();
		Doctor doctor = doctorService.findDoctorById(dto.doctorId());
		Appointment appointment = appointmentMapper.toFirstAppointment(dto, doctor, patient);
		appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);
		return appointmentRepository.save(appointment).getId();
	}

	@Override
	@Transactional
	public Long createDraft(AppointmentCreateDto dto) {
		Appointment appointment;
		if (dto.referralId() != null) {
			Referral referral = referralService.getActiveById(dto.referralId());
			appointment = appointmentMapper.toAppointment(dto, referral.getIssuedBy(), referral.getPatient(), referral);
			appointment.setType(referral.getAppointmentType());
			appointment.setStatus(AppointmentStatus.SCHEDULED);
		}
		else {
			Patient patient = patientService.getCurrentPatient();
			Doctor doctor = doctorService.findDoctorById(dto.doctorId());
			appointment = appointmentMapper.toAppointment(dto, doctor, patient, null);
			appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);
		}
		return appointmentRepository.save(appointment).getId();
	}

	@Override
	@Transactional
	public void confirm(Long appointmentId) {
		Appointment appointment = findAppointment(appointmentId);
		if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
			throw new AppointmentException("Only draft appointment can be confirmed");
		}
		appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);
		if (appointment.getReferral() != null) {
			referralService.markUsed(appointment.getReferral());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Appointment getConfirmed(Long appointmentId) {
		Appointment appointment = findAppointment(appointmentId);
		if (appointment.getStatus() != AppointmentStatus.SCHEDULED_CONFIRMED) {
			throw new AppointmentException("Appointment is not confirmed");
		}
		return appointment;
	}

	@Override
	@Transactional
	public void createWithLabs(AppointmentCreateWithLabsDto req) {
		Patient patient = patientService.getById(req.patientId());
		Doctor doctor = doctorService.getCurrentDoctor();

		Set<Long> uniqueIds = new LinkedHashSet<>(req.labTestIds());
		List<LabTest> tests = labTestService.findAllById(uniqueIds);

		Appointment appointment = appointmentMapper.toLabAppointment(req, doctor, patient);
		appointment.setType(AppointmentType.TEST);
		appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);

		appointmentRepository.save(appointment);
		labResultService.createForAppointment(appointment, tests);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PatientLabHistoryDTO> getPatientLabHistory(Long userId, Pageable pageable) {
		return appointmentRepository.findLabHistoryPage(userId, pageable).map(labResultMapper::toHistoryDto);
	}

	@Override
	@Transactional(readOnly = true)
	public AppointmentStatusCounts getAllStatusStatistics(LocalDate startDate, LocalDate endDate) {
		return appointmentRepository.getAllStatusStatistics(startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public AppointmentStatusCounts getStatusStatisticsByType(LocalDate startDate, LocalDate endDate,
			AppointmentType type) {
		return appointmentRepository.getStatusStatisticsByType(startDate, endDate, type);
	}

	private Appointment findAppointment(Long appointmentId) {
		return appointmentRepository.findById(appointmentId)
			.orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
	}

}
