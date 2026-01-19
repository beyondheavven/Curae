package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.mapper.AppointmentMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.labresult.mapper.LabResultMapper;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.service.LabTestService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;

	private final LabResultMapper labResultMapper;

	private final AppointmentMapper appointmentMapper;

	private final PatientService patientService;

	private final LabTestService labTestService;

	private final LabResultService labResultService;

	@Override
	@Transactional(readOnly = true)
	public Page<PatientLabHistoryDTO> getPatientLabHistory(Long userId, Pageable pageable) {
		return appointmentRepository.findLabHistoryPage(userId, pageable).map(labResultMapper::toHistoryDto);
	}

	@Override
	@Transactional
	public void createWithLabs(AppointmentCreateWithLabsDto req) {
		Patient patient = patientService.getById(req.patientId());

		Set<Long> uniqueIds = new LinkedHashSet<>(req.labTestIds());

		List<LabTest> tests = labTestService.findAllById(uniqueIds);
		Appointment appointment = appointmentMapper.toEntity(req, patient);

		appointmentRepository.save(appointment);

		labResultService.createForAppointment(appointment, tests);

	}

}
