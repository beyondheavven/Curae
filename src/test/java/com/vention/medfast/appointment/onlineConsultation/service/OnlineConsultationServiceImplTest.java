package com.vention.medfast.appointment.onlineConsultation.service;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.mapper.OnlineConsultationMapper;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.OnlineConsultation;
import com.vention.medfast.appointment.repository.OnlineConsultationRepository;
import com.vention.medfast.appointment.service.OnlineConsultationServiceImpl;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OnlineConsultationServiceImplTest {

	@Mock
	private OnlineConsultationRepository onlineConsultationRepository;

	@Mock
	private OnlineConsultationMapper onlineConsultationMapper;

	@Mock
	private PatientService patientService;

	@Mock
	private DoctorService doctorService;

	@InjectMocks
	private OnlineConsultationServiceImpl service;

	@Test
	void getDetailsReturnsDtoFromRepository() {
		OnlineConsultationDetails details = new OnlineConsultationDetails(1L, 2L, 3L, LocalDate.now(), LocalTime.NOON,
				AppointmentStatus.SCHEDULED, null, null, "link");

		when(onlineConsultationRepository.findDetailsById(1L)).thenReturn(Optional.of(details));

		OnlineConsultationDetails result = service.getDetails(1L);

		assertThat(result).isSameAs(details);
		verify(onlineConsultationRepository).findDetailsById(1L);
		verifyNoInteractions(onlineConsultationMapper);
	}

	@Test
    void getDetailsThrowsWhenNotFound() {
        when(onlineConsultationRepository.findDetailsById(10L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDetails(10L))
            .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(onlineConsultationMapper);
    }

	@Test
	void createBuildsEntityAndSaves() {
		CreateOnlineConsultationRequest req = new CreateOnlineConsultationRequest(5L, 6L, LocalDate.now().plusDays(1),
				LocalTime.NOON, AppointmentStatus.SCHEDULED, "link");

		Patient patient = new Patient();
		Doctor doctor = new Doctor();
		OnlineConsultation oc = new OnlineConsultation();

		when(patientService.getById(5L)).thenReturn(patient);
		when(doctorService.findDoctorById(6L)).thenReturn(doctor);
		when(onlineConsultationMapper.toEntity(req)).thenReturn(oc);

		service.create(req);

		assertThat(oc.getPatient()).isSameAs(patient);
		assertThat(oc.getDoctor()).isSameAs(doctor);

		verify(onlineConsultationMapper).toEntity(req);
		verify(onlineConsultationRepository).save(oc);
	}

	@Test
	void deleteDeletesExistingEntity() {
		OnlineConsultation oc = new OnlineConsultation();

		when(onlineConsultationRepository.findById(3L)).thenReturn(Optional.of(oc));

		service.deleteById(3L);

		verify(onlineConsultationRepository).delete(oc);
	}

	@Test
    void deleteThrowsWhenNotFound() {
        when(onlineConsultationRepository.findById(8L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(8L))
            .isInstanceOf(EntityNotFoundException.class);
    }

	@Test
	void updateUpdatesEntityAndChangesDoctor() {
		UpdateOnlineConsultationRequest req = new UpdateOnlineConsultationRequest(9L, 5L, 6L,
				LocalDate.now().plusDays(2), LocalTime.of(15, 0), AppointmentStatus.IN_CONSULTATION, "new");

		Doctor oldDoctor = new Doctor();
		oldDoctor.setId(7L);

		OnlineConsultation oc = new OnlineConsultation();
		oc.setDoctor(oldDoctor);

		Doctor newDoctor = new Doctor();
		newDoctor.setId(6L);

		when(onlineConsultationRepository.findById(9L)).thenReturn(Optional.of(oc));
		when(doctorService.findDoctorById(6L)).thenReturn(newDoctor);

		service.update(req);

		verify(onlineConsultationMapper).updateOnlineConsultation(req, oc);
		verify(doctorService).findDoctorById(6L);
		verify(onlineConsultationRepository).save(oc);

		assertThat(oc.getDoctor()).isSameAs(newDoctor);
	}

	@Test
	void updateThrowsWhenNotFound() {
		UpdateOnlineConsultationRequest req = new UpdateOnlineConsultationRequest(1L, 1L, 1L, LocalDate.now(),
				LocalTime.NOON, AppointmentStatus.SCHEDULED, "l");

		when(onlineConsultationRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.update(req)).isInstanceOf(EntityNotFoundException.class);

		verifyNoInteractions(onlineConsultationMapper);
		verifyNoInteractions(doctorService);
	}

}
