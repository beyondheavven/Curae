package com.vention.medfast.appointment.service.cache;

import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.appointment.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitCacheServiceTest {

	@Mock
	private VisitRepository visitRepository;

	@InjectMocks
	private VisitCacheService visitCacheService;

	@Test
	void getVisits_whenTypeIsPAST_callsFindPastByPatient() {
		Long patientId = 1L;
		int page = 0;
		int size = 10;

		Slice<VisitListItemDto> expected = new SliceImpl<>(List.of());

		when(visitRepository.findPastByPatient(eq(patientId), any(LocalDate.class), any(LocalTime.class),
				any(PageRequest.class)))
			.thenReturn(expected);

		Slice<VisitListItemDto> result = visitCacheService.getVisits(patientId, VisitType.PAST, page, size);

		assertThat(result).isSameAs(expected);

		verify(visitRepository).findPastByPatient(eq(patientId), any(LocalDate.class), any(LocalTime.class),
				any(PageRequest.class));
	}

	@Test
	void getVisits_whenTypeIsUPCOMING_callsFindUpcomingByPatient() {
		Long patientId = 2L;
		int page = 1;
		int size = 5;

		Slice<VisitListItemDto> expected = new SliceImpl<>(List.of());

		when(visitRepository.findUpcomingByPatient(eq(patientId), any(LocalDate.class), any(LocalTime.class),
				any(PageRequest.class)))
			.thenReturn(expected);

		Slice<VisitListItemDto> result = visitCacheService.getVisits(patientId, VisitType.UPCOMING, page, size);

		assertThat(result).isSameAs(expected);

		verify(visitRepository).findUpcomingByPatient(eq(patientId), any(LocalDate.class), any(LocalTime.class),
				any(PageRequest.class));
	}

}
