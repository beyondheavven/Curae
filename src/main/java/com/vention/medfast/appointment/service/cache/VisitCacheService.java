package com.vention.medfast.appointment.service.cache;

import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.appointment.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import com.vention.medfast.wrapper.CachedSlice;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitCacheService {

	private final VisitRepository visitRepository;

	@Cacheable(cacheNames = "patientVisits",
			key = "'patient:' + #patientId + ':type:' + #type + ':page:' + #page + ':size:' + #size", sync = true)
	public CachedSlice<VisitListItemDto> getVisits(Long patientId, VisitType type, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		LocalDateTime now = LocalDateTime.now();

		Slice<VisitListItemDto> slice = switch (type) {
			case PAST -> visitRepository.findPastByPatient(patientId, now.toLocalDate(), now.toLocalTime(), pageable);
			case UPCOMING ->
				visitRepository.findUpcomingByPatient(patientId, now.toLocalDate(), now.toLocalTime(), pageable);
		};

		return new CachedSlice<>(slice.getContent(), page, size, slice.hasNext());
	}

}
