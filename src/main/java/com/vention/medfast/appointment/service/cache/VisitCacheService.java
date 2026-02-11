package com.vention.medfast.appointment.service.cache;

import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.appointment.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitCacheService {

	private final VisitRepository visitRepository;

	@Cacheable(cacheNames = "patientVisits", key = "T(java.util.Objects).hash(#patientId, #type, #page, #size)",
			sync = true)
	public Slice<VisitListItemDto> getVisits(Long patientId, VisitType type, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		LocalDateTime now = LocalDateTime.now();

		return switch (type) {
			case PAST -> visitRepository.findPastByPatient(patientId, now.toLocalDate(), now.toLocalTime(), pageable);
			case UPCOMING ->
				visitRepository.findUpcomingByPatient(patientId, now.toLocalDate(), now.toLocalTime(), pageable);
		};
	}

}
