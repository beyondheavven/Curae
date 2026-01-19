package com.vention.medfast.labresult.service;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.labtest.model.LabTest;
import java.util.List;

public interface LabResultService {

	void createForAppointment(Appointment appointment, List<LabTest> tests);

}
