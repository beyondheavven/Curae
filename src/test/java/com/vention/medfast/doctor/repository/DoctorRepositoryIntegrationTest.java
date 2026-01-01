package com.vention.medfast.doctor.repository;

import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.address.repository.AddressRepository;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.repository.ClinicRepository;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.util.DataUtils;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.repository.SpecializationRepository;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class DoctorRepositoryIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private ClinicRepository clinicRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private DoctorDetailsRepository doctorDetailsRepository;

	@Autowired
	private SpecializationRepository specializationRepository;

	@Test
	void shouldLoadExistingManyToManyData() {
		Doctor doctor = doctorRepository.findById(1L).orElseThrow();

		assertThat(doctor.getBio()).isEqualTo("World-renowned diagnostician with unconventional methods.");
		assertThat(doctor.getClinics()).hasSize(1);
		assertThat(doctor.getClinics().iterator().next().getName()).isEqualTo("Central City Hospital");
	}

	@Test
	void shouldSaveNewDoctorWithRelationships() {
		Clinic existingClinic = clinicRepository.findById(1L).orElseThrow();
		Specialization specialization = specializationRepository.findAll().getFirst();

		User savedUser = userRepository.save(DataUtils.getDoctorUserTransient());

		Doctor doctorToSave = DataUtils.getDoctorForIntegration(savedUser, specialization);
		doctorToSave.addClinic(existingClinic);

		Doctor savedDoctor = doctorRepository.save(doctorToSave);

		assertThat(savedDoctor.getId()).isNotNull();
		assertThat(savedDoctor.getDetails().getId()).isEqualTo(savedDoctor.getId());
		assertThat(savedDoctor.getClinics()).hasSize(1);
		assertThat(savedDoctor.getSpecializations()).contains(specialization);
	}

	@Test
	void shouldUpdateClinicList() {
		Doctor doctor = doctorRepository.findById(2L).orElseThrow();
		Address address = addressRepository.findById(2L).orElseThrow();
		address.setCity("Warsaw");

		Clinic newClinic = new Clinic();
		newClinic.setName("ALab");
		newClinic.setAddress(address);
		clinicRepository.save(newClinic);

		doctor.addClinic(newClinic);
		Doctor updatedDoctor = doctorRepository.save(doctor);

		assertThat(updatedDoctor.getClinics()).hasSize(2);
		assertThat(updatedDoctor.getClinics()).extracting(Clinic::getName).contains("Central City Hospital", "ALab");
	}

	@Test
	void shouldCascadeDelete() {
		User user = userRepository.save(DataUtils.getDoctorUserTransient());
		Clinic clinic = clinicRepository.findById(1L).orElseThrow();
		Specialization specialization = specializationRepository.findAll().getFirst();

		Doctor doctorToDelete = DataUtils.getDoctorForIntegration(user, specialization);
		doctorToDelete.addClinic(clinic);

		Doctor savedDoctor = doctorRepository.save(doctorToDelete);
		Long doctorId = savedDoctor.getId();

		doctorRepository.deleteById(doctorId);

		assertThat(doctorRepository.findById(doctorId)).isEmpty();
		assertThat(doctorDetailsRepository.findById(doctorId)).isEmpty();
		assertThat(userRepository.findById(user.getId())).isPresent();
		assertThat(clinicRepository.findById(1L)).isPresent();
	}

}
