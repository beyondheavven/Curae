package com.vention.medfast.labresult.service;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.labresult.model.LabResult;
import com.vention.medfast.labresult.model.LabResultStatus;
import com.vention.medfast.labresult.repository.LabResultRepository;
import com.vention.medfast.labtest.model.LabTest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LabResultServiceImpl implements LabResultService {

	private final LabResultRepository labResultRepository;

	@Override
	public void createForAppointment(Appointment appointment, List<LabTest> tests) {
		List<LabResult> results = tests.stream().map(test -> {
			LabResult lr = new LabResult();
			lr.setAppointment(appointment);
			lr.setLabTest(test);
			lr.setStatus(LabResultStatus.PROCESSING);
			return lr;
		}).toList();

		labResultRepository.saveAll(results);
	}

}
