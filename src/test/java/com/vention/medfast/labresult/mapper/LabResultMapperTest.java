package com.vention.medfast.labresult.mapper;

import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.labresult.model.LabResult;
import com.vention.medfast.labresult.model.LabResultStatus;
import com.vention.medfast.labtest.model.LabTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class LabResultMapperTest {

	private LabResultMapper labResultMapper;

	@BeforeEach
	void setUp() {
		labResultMapper = Mappers.getMapper(LabResultMapper.class);
	}

	@Test
	void shouldMapAppointmentToHistoryDto() {
		com.vention.medfast.appointment.model.Test testAppointment = new com.vention.medfast.appointment.model.Test();
		testAppointment.setId(10L);
		testAppointment.setDate(LocalDate.of(2030, 2, 15));
		testAppointment.setTime(LocalTime.of(8, 45));
		testAppointment.setStatus(AppointmentStatus.COMPLETED);

		LabTest labTest = new LabTest();
		labTest.setId(3L);
		labTest.setName("Blood Test");

		LabResult labResult = new LabResult();
		labResult.setId(7L);
		labResult.setTest(testAppointment);
		labResult.setLabTest(labTest);
		labResult.setStatus(LabResultStatus.PROCESSING);
		labResult.setResultUrl("http://result.url");
		labResult.setUploadedAt(LocalDateTime.of(2030, 2, 16, 9, 0));

		testAppointment.setLabResults(List.of(labResult));

		PatientLabHistoryDTO dto = labResultMapper.toHistoryDto(testAppointment);

		assertThat(dto.appointmentId()).isEqualTo(10L);
		assertThat(dto.date()).isEqualTo(testAppointment.getDate());
		assertThat(dto.appointmentStatus()).isEqualTo(AppointmentStatus.COMPLETED.name());
		assertThat(dto.tests()).hasSize(1);
		assertThat(dto.tests().get(0).id()).isEqualTo(7L);
		assertThat(dto.tests().get(0).testName()).isEqualTo("Blood Test");
		assertThat(dto.tests().get(0).status()).isEqualTo(LabResultStatus.PROCESSING);
		assertThat(dto.tests().get(0).resultUrl()).isEqualTo("http://result.url");
		assertThat(dto.tests().get(0).uploadedAt()).isEqualTo(labResult.getUploadedAt());
	}

}
