package com.vention.curae.clinic.service;

import com.vention.curae.address.model.Address;
import com.vention.curae.address.repository.AddressRepository;
import com.vention.curae.clinic.dto.ClinicRequestDto;
import com.vention.curae.clinic.dto.ClinicResponseDto;
import com.vention.curae.clinic.mapper.ClinicMapper;
import com.vention.curae.clinic.model.Clinic;
import com.vention.curae.clinic.repository.ClinicRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

	private final ClinicRepository clinicRepository;

	private final AddressRepository addressRepository;

	private final ClinicMapper clinicMapper;

	@Override
	@Transactional(readOnly = true)
	public List<ClinicResponseDto> getAll() {
		return clinicRepository.findAll().stream().map(clinicMapper::toResponseDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ClinicResponseDto getById(Long id) {
		return clinicMapper.toResponseDto(findById(id));
	}

	@Override
	@Transactional
	public Long create(ClinicRequestDto dto) {
		Clinic clinic = clinicMapper.toEntity(dto);
		Address address = addressRepository.save(clinicMapper.toAddress(dto.address()));
		clinic.setAddress(address);
		return clinicRepository.save(clinic).getId();
	}

	@Override
	@Transactional
	public Long update(Long id, ClinicRequestDto dto) {
		Clinic clinic = findById(id);
		clinicMapper.updateEntity(dto, clinic);
		if (clinic.getAddress() == null) {
			Address address = addressRepository.save(clinicMapper.toAddress(dto.address()));
			clinic.setAddress(address);
		}
		else {
			clinicMapper.updateAddress(dto.address(), clinic.getAddress());
			addressRepository.save(clinic.getAddress());
		}
		return clinicRepository.save(clinic).getId();
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Clinic clinic = findById(id);
		Address address = clinic.getAddress();
		clinicRepository.delete(clinic);
		if (address != null) {
			addressRepository.delete(address);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Clinic> findAllByIds(Set<Long> clinicIds) {
		return clinicRepository.findAllById(clinicIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Clinic findById(Long id) {
		return clinicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Clinic not found"));
	}

}
