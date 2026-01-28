package com.vention.medfast.appointment.onlineConsultation.service;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.mapper.OnlineConsultationMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.OnlineConsultation;
import com.vention.medfast.appointment.repository.AppointmentRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void getOnlineConsultationReturnsMappedDto() {
        OnlineConsultation oc = new OnlineConsultation();
        OnlineConsultationDetails details = new OnlineConsultationDetails(1L, 2L, 3L, LocalDate.now(), LocalTime.NOON,
            AppointmentStatus.SCHEDULED, AppointmentType.ONLINE, null, "link");

        when(onlineConsultationRepository.findById(1L)).thenReturn(Optional.of(oc));
        when(onlineConsultationMapper.toDto(oc)).thenReturn(details);

        OnlineConsultationDetails result = service.getDetails(1L);

        assertThat(result).isEqualTo(details);
        verify(onlineConsultationRepository).findById(1L);
        verify(onlineConsultationMapper).toDto(oc);
    }

	@Test
	void getOnlineConsultationThrowsWhenNotFound() {
		when(onlineConsultationRepository.findById(10L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getDetails(10L)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void createBuildsEntitiesAndSaves() {
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
		verify(onlineConsultationRepository).save(oc);
		verify(onlineConsultationMapper).toEntity(req);
	}

	@Test
	void deleteOnlineConsultationDeletesAppointment() {
		OnlineConsultation oc = new OnlineConsultation();
		when(onlineConsultationRepository.findById(3L)).thenReturn(Optional.of(oc));

		service.deleteById(3L);

		verify(onlineConsultationRepository).delete(oc);
	}

	@Test
	void deleteOnlineConsultationThrowsWhenNotFound() {
		when(onlineConsultationRepository.findById(8L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.deleteById(8L)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void updateDelegatesToMapperAndSetsDoctor() {
        UpdateOnlineConsultationRequest req = new UpdateOnlineConsultationRequest(9L, 5L, 6L,
            LocalDate.now().plusDays(2), LocalTime.of(15, 0), AppointmentStatus.IN_CONSULTATION, "new");

        OnlineConsultation oc = new OnlineConsultation();

        Doctor oldDoctor = new Doctor();
        oldDoctor.setId(7L);
        oc.setDoctor(oldDoctor);

        Doctor newDoctor = new Doctor();
        newDoctor.setId(6L);

        when(onlineConsultationRepository.findById(9L)).thenReturn(Optional.of(oc));
        when(doctorService.findDoctorById(6L)).thenReturn(newDoctor);

        service.update(req);
        verify(onlineConsultationMapper).updateOnlineConsultation(req, oc);
        assertThat(oc.getDoctor()).isSameAs(newDoctor);
        verify(doctorService).findDoctorById(6L);
        verify(onlineConsultationRepository).save(oc);
	}

	@Test
	void updateThrowsWhenNotFound() {
		UpdateOnlineConsultationRequest req = new UpdateOnlineConsultationRequest(1L, 1L, 1L, LocalDate.now(),
				LocalTime.NOON, AppointmentStatus.SCHEDULED, "l");
		when(onlineConsultationRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.update(req)).isInstanceOf(EntityNotFoundException.class);
		verifyNoInteractions(onlineConsultationMapper);
	}

}
