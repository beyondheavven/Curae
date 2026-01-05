package com.vention.medfast.globalsearch.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vention.medfast.doctor.dto.DoctorBaseDto;
import com.vention.medfast.doctor.mapper.DoctorMapper;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.QDoctor;
import com.vention.medfast.globalsearch.dto.SearchRequest;
import com.vention.medfast.globalsearch.dto.SearchResponseDto;
import com.vention.medfast.service.dto.ProvidedServiceBaseDto;
import com.vention.medfast.service.mapper.ProvidedServiceMapper;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.model.QProvidedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private JPAQueryFactory queryFactory;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private ProvidedServiceMapper providedServiceMapper;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private JPAQuery<Doctor> doctorQuery;
    @Mock
    private JPAQuery<ProvidedService> serviceQuery;

    @BeforeEach
    void setUp() {
        lenient().when(queryFactory.selectFrom(any(QDoctor.class))).thenReturn(doctorQuery);

        lenient().when(doctorQuery.leftJoin(any(EntityPathBase.class), any())).thenReturn(doctorQuery);
        lenient().when(doctorQuery.fetchJoin()).thenReturn(doctorQuery);
        lenient().when(doctorQuery.where(any(Predicate.class))).thenReturn(doctorQuery);
        lenient().when(doctorQuery.distinct()).thenReturn(doctorQuery);
        lenient().when(doctorQuery.orderBy(any(com.querydsl.core.types.OrderSpecifier.class))).thenReturn(doctorQuery);
        lenient().when(doctorQuery.limit(any(Long.class))).thenReturn(doctorQuery);

        lenient().when(queryFactory.selectFrom(any(QProvidedService.class))).thenReturn(serviceQuery);

        lenient().when(serviceQuery.where(any(Predicate.class))).thenReturn(serviceQuery);
        lenient().when(serviceQuery.distinct()).thenReturn(serviceQuery);
        lenient().when(serviceQuery.orderBy(any(com.querydsl.core.types.OrderSpecifier.class))).thenReturn(serviceQuery);
        lenient().when(serviceQuery.limit(any(Long.class))).thenReturn(serviceQuery);
    }

    @Test
    @DisplayName("Should return mapped doctors and services when query matches data")
    void shouldReturnResultsWhenQueryIsValid() {
        String queryText = "House";
        SearchRequest request = new SearchRequest(queryText);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        List<Doctor> foundDoctors = List.of(doctor);

        ProvidedService service = new ProvidedService();
        service.setId(10L);
        List<ProvidedService> foundServices = List.of(service);

        when(doctorQuery.fetch()).thenReturn(foundDoctors);
        when(serviceQuery.fetch()).thenReturn(foundServices);

        DoctorBaseDto doctorDto = DoctorBaseDto.builder().firstName("Gregory").lastName("House").build();
        ProvidedServiceBaseDto serviceDto = ProvidedServiceBaseDto.builder().name("MRI").build();

        when(doctorMapper.toDtoList(foundDoctors)).thenReturn(List.of(doctorDto));
        when(providedServiceMapper.toDtoList(foundServices)).thenReturn(List.of(serviceDto));

        SearchResponseDto result = searchService.performGlobalSearch(request);

        assertThat(result).isNotNull();
        assertThat(result.doctors()).hasSize(1);
        assertThat(result.doctors().getFirst().getLastName()).isEqualTo("House");
        assertThat(result.services()).hasSize(1);
        assertThat(result.services().getFirst().name()).isEqualTo("MRI");

        verify(queryFactory).selectFrom(QDoctor.doctor);
        verify(queryFactory).selectFrom(QProvidedService.providedService);
        verify(doctorMapper).toDtoList(foundDoctors);
    }

    @Test
    @DisplayName("Should return empty lists when no matches found")
    void shouldReturnEmptyWhenNoMatches() {
        String queryText = "Unknown";
        SearchRequest request = new SearchRequest(queryText);

        when(doctorQuery.fetch()).thenReturn(Collections.emptyList());
        when(serviceQuery.fetch()).thenReturn(Collections.emptyList());

        when(doctorMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(providedServiceMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        SearchResponseDto result = searchService.performGlobalSearch(request);

        assertThat(result.doctors()).isEmpty();
        assertThat(result.services()).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty query string (return general results or empty depending on logic)")
    void shouldHandleEmptyQueryString() {
        SearchRequest request = new SearchRequest("");

        when(doctorQuery.fetch()).thenReturn(Collections.emptyList());
        when(serviceQuery.fetch()).thenReturn(Collections.emptyList());

        searchService.performGlobalSearch(request);

        verify(queryFactory).selectFrom(QDoctor.doctor);
        verify(doctorQuery).fetch();
    }
}
