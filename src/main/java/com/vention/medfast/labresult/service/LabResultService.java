package com.vention.medfast.labresult.service;

import com.vention.medfast.appointment.model.Test;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.statistics.dto.response.LabResultStatusCounts;
import java.time.LocalDate;
import java.util.List;

public interface LabResultService {

	void createForAppointment(Test test, List<LabTest> tests);

	LabResultStatusCounts getStatusStatistics(LocalDate startDate, LocalDate endDate);

	LabResultStatusCounts getStatusCountsByClinic(Long clinicId, LocalDate startDate, LocalDate endDate);

}
