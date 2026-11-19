package com.vention.curae.appointment.service;

import com.vention.curae.appointment.dto.PatientLabHistoryDTO;
import com.vention.curae.appointment.dto.StatusStatisticDto;
import com.vention.curae.appointment.dto.TestCreateWithLabsDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TestService {

	Long createWithLabs(TestCreateWithLabsDto dto);

	Page<PatientLabHistoryDTO> getPatientLabHistory(Long patientId, Pageable pageable);

	List<StatusStatisticDto> getStatusStatisticsByClinic();

}
