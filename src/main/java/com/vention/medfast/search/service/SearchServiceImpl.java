package com.vention.medfast.search.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vention.medfast.clinic.model.QClinic;
import com.vention.medfast.doctor.mapper.DoctorMapper;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.QDoctor;
import com.vention.medfast.search.dto.SearchResponse;

import com.vention.medfast.service.mapper.ProvidedServiceMapper;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.model.QProvidedService;
import com.vention.medfast.specialization.model.QSpecialization;
import com.vention.medfast.user.model.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

	private final JPAQueryFactory queryFactory;

	private final DoctorMapper doctorMapper;

	private final ProvidedServiceMapper providedServiceMapper;

	@Transactional(readOnly = true)
	@Override
	public SearchResponse performGlobalSearch(String query) {

		String searchText = (query == null) ? "" : query.trim().toLowerCase();

		if (searchText.isEmpty()) {
			return new SearchResponse(Collections.emptyList(), Collections.emptyList());
		}

		List<Doctor> doctorEntities = findDoctorEntities(searchText);

		List<ProvidedService> serviceEntities = findServiceEntities(searchText);

		return SearchResponse.builder()
			.doctors(doctorMapper.toDtoList(doctorEntities))
			.services(providedServiceMapper.toDtoList(serviceEntities))
			.build();
	}

	private List<Doctor> findDoctorEntities(String searchText) {
		QDoctor d = QDoctor.doctor;
		QUser u = QUser.user;
		QSpecialization s = QSpecialization.specialization;

		return queryFactory.selectFrom(d)
			.leftJoin(d.user, u)
			.fetchJoin()
			.leftJoin(d.specializations, s)
			.fetchJoin()
			.leftJoin(d.clinics, QClinic.clinic)
			.fetchJoin()
			.where(u.firstName.containsIgnoreCase(searchText)
				.or(u.lastName.containsIgnoreCase(searchText))
				.or(s.name.containsIgnoreCase(searchText)))
			.distinct()
			.limit(5)
			.fetch();
	}

	private List<ProvidedService> findServiceEntities(String searchText) {
		QProvidedService srv = QProvidedService.providedService;

		return queryFactory.selectFrom(srv).where(srv.name.containsIgnoreCase(searchText)).limit(5).fetch();
	}

}
