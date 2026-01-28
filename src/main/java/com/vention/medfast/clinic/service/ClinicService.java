package com.vention.medfast.clinic.service;

import com.vention.medfast.clinic.model.Clinic;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public interface ClinicService {

	List<Clinic> findAllByIds(Set<Long> clinicIds);

	Clinic findById(Long clinicId);

}
