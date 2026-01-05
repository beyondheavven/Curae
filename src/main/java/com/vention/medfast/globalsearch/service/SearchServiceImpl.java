package com.vention.medfast.globalsearch.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vention.medfast.doctor.mapper.DoctorMapper;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.QDoctor;
import com.vention.medfast.globalsearch.dto.SearchRequest;
import com.vention.medfast.globalsearch.dto.SearchResponseDto;

import com.vention.medfast.service.mapper.ProvidedServiceMapper;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.model.QProvidedService;
import com.vention.medfast.specialization.model.QSpecialization;
import com.vention.medfast.user.model.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

	private final JPAQueryFactory queryFactory;

	private final DoctorMapper doctorMapper;

	private final ProvidedServiceMapper providedServiceMapper;

	@Transactional(readOnly = true)
	@Override
	public SearchResponseDto performGlobalSearch(SearchRequest request) {

		String searchText = request.query();

		List<Doctor> doctorEntities = findDoctorEntities(searchText);

		List<ProvidedService> serviceEntities = findServiceEntities(searchText);
		return SearchResponseDto.builder()
			.doctors(doctorMapper.toDtoList(doctorEntities))
			.services(providedServiceMapper.toDtoList(serviceEntities))
			.build();
	}

	private List<Doctor> findDoctorEntities(String searchText) {
		QDoctor qDoctor = QDoctor.doctor;
		QUser qUser = QUser.user;
		QSpecialization qSpecialization = QSpecialization.specialization;

		BooleanBuilder builder = new BooleanBuilder();

		if (searchText != null && !searchText.isBlank()) {
			builder.or(qDoctor.user.firstName.containsIgnoreCase(searchText));
			builder.or(qDoctor.user.lastName.containsIgnoreCase(searchText));
			builder.or(qDoctor.specializations.any().name.containsIgnoreCase(searchText));
		}

		return queryFactory.selectFrom(qDoctor)
			.leftJoin(qDoctor.user, qUser)
			.fetchJoin()
			.leftJoin(qDoctor.specializations, qSpecialization)
			.fetchJoin()
			.where(builder)
			.distinct()
			.orderBy(qDoctor.user.firstName.asc())
			.limit(10)
			.fetch();
	}

	private List<ProvidedService> findServiceEntities(String searchText) {
		QProvidedService srv = QProvidedService.providedService;

		BooleanBuilder builder = new BooleanBuilder();
		if (searchText != null && !searchText.isBlank()) {
			builder.and(srv.name.containsIgnoreCase(searchText));
		}

		return queryFactory.selectFrom(srv).where(builder).distinct().orderBy(srv.name.asc()).limit(10).fetch();
	}

}
