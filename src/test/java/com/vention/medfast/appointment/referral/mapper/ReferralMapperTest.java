package com.vention.medfast.appointment.referral.mapper;

import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.referral.dto.ReferralCreateDto;
import com.vention.medfast.referral.dto.ReferralResponseDto;
import com.vention.medfast.referral.mapper.ReferralMapper;
import com.vention.medfast.referral.model.Referral;
import com.vention.medfast.referral.model.ReferralStatus;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReferralMapperTest {

	private ReferralMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = Mappers.getMapper(ReferralMapper.class);
	}

	@Test
	void toEntityMapsAllRequiredFieldsAndSetsActiveStatus() {
		ReferralCreateDto dto = new ReferralCreateDto(1L, 2L, AppointmentType.VISIT, LocalDate.now().plusDays(7));
		Patient patient = new Patient();
		patient.setId(1L);
		Doctor doctor = new Doctor();
		doctor.setId(2L);
		ProvidedService service = new ProvidedService();
		service.setId(3L);
		service.setName("Blood test");
		service.setDescription("Basic");
		service.setPrice(BigDecimal.ONE);
		service.setDuration(15);

		Referral referral = mapper.toEntity(dto, patient, doctor, service);

		assertThat(referral.getId()).isNull();
		assertThat(referral.getPatient()).isSameAs(patient);
		assertThat(referral.getIssuedBy()).isSameAs(doctor);
		assertThat(referral.getService()).isSameAs(service);
		assertThat(referral.getAppointmentType()).isEqualTo(AppointmentType.VISIT);
		assertThat(referral.getExpiresAt()).isEqualTo(dto.expiresAt());
		assertThat(referral.getStatus()).isEqualTo(ReferralStatus.ACTIVE);
	}

	@Test
	void toDtoCopiesIdServiceIdExpiresAtAndStatus() {
		Referral referral = new Referral();
		referral.setId(9L);
		referral.setExpiresAt(LocalDate.now().plusDays(3));
		referral.setStatus(ReferralStatus.USED);
		ProvidedService service = new ProvidedService();
		service.setId(99L);
		referral.setService(service);

		ReferralResponseDto dto = mapper.toDto(referral);

		assertThat(dto.id()).isEqualTo(referral.getId());
		assertThat(dto.serviceId()).isEqualTo(service.getId());
		assertThat(dto.expiresAt()).isEqualTo(referral.getExpiresAt());
		assertThat(dto.status()).isEqualTo(referral.getStatus());
	}

}
