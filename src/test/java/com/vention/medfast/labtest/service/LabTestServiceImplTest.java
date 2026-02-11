package com.vention.medfast.labtest.service;

import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.repository.LabTestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabTestServiceImplTest {

	@Mock
	private LabTestRepository labTestRepository;

	@InjectMocks
	private LabTestServiceImpl service;

	@Test
	void findAllById_shouldReturnTests_whenAllIdsExist() {
		LabTest t1 = new LabTest();
		LabTest t2 = new LabTest();

		Set<Long> ids = Set.of(1L, 2L);

		when(labTestRepository.findAllById(ids)).thenReturn(List.of(t1, t2));

		List<LabTest> result = service.findAllById(ids);

		assertThat(result).hasSize(2);
	}

	@Test
	void findAllById_shouldThrow_whenSomeIdsAreMissing() {
		LabTest t1 = new LabTest();

		Set<Long> ids = Set.of(1L, 2L);

		when(labTestRepository.findAllById(ids)).thenReturn(List.of(t1));

		assertThatThrownBy(() -> service.findAllById(ids)).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Some lab_test_ids are invalid");
	}

}
