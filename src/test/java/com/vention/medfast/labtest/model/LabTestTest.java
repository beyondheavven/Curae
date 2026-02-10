package com.vention.medfast.labtest.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LabTestTest {

	@Test
	void shouldCreateAndReadLabTestEntity() {
		LabTest labTest = new LabTest();

		labTest.setId(1L);
		labTest.setName("Blood Test");
		labTest.setDescription("General blood analysis");
		labTest.setPrice(BigDecimal.valueOf(99.99));

		assertThat(labTest.getId()).isEqualTo(1L);
		assertThat(labTest.getName()).isEqualTo("Blood Test");
		assertThat(labTest.getDescription()).isEqualTo("General blood analysis");
		assertThat(labTest.getPrice()).isEqualByComparingTo("99.99");
	}

}
