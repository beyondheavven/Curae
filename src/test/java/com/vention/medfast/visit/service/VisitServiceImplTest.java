package com.vention.medfast.visit.service;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.mapper.VisitMapper;
import com.vention.medfast.visit.model.Visit;
import com.vention.medfast.visit.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.vention.medfast.appointment.model.AppointmentType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitServiceImplTest {

	@Mock
	private VisitMapper visitMapper;

    @Mock
    private AppointmentService appointmentService;

	@Mock
	private VisitRepository visitRepository;

	@InjectMocks
	private VisitServiceImpl visitService;

	@Test
	public void shouldCreateVisitWhenInputIsValid() {
        VisitCreateDto dto = new VisitCreateDto(1L, "Diagnosis", "Symptoms", "Medical report");
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Status.SCHEDULED_CONFIRMED);
        appointment.setType(AppointmentType.VISIT);
        Visit visit = new Visit();
        visit.setAppointment(appointment);
        when(appointmentService.getConfirmed(1L)).thenReturn(appointment);
        when(visitRepository.existsById(1L)).thenReturn(false);
        when(visitMapper.toEntity(dto, appointment)).thenReturn(visit);
        when(visitRepository.save(visit)).thenAnswer(invocation -> {
            Visit v = invocation.getArgument(0);
            v.setId(1L);
            return v;
        });
        long resultId = visitService.create(dto);
        assertEquals(1L, resultId);
        assertEquals(Status.COMPLETED, appointment.getStatus());
        verify(appointmentService).getConfirmed(1L);
        verify(visitRepository).existsById(1L);
        verify(visitMapper).toEntity(dto, appointment);
        verify(visitRepository).save(visit);
	}

}
