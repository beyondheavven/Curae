package com.vention.medfast.clinic.service;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.repository.ClinicRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

	private final ClinicRepository clinicRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Clinic> findAllByIds(Set<Long> clinicIds) {
		return clinicRepository.findAllById(clinicIds);
	}

	@Override
	public Clinic findById(Long id) {
		return clinicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Clinic not found"));
	}

}
