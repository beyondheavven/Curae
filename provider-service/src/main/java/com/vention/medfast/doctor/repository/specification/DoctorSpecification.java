package com.vention.medfast.doctor.repository.specification;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.dto.DoctorSearchDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.provider.model.UserSnapshot;
import com.vention.medfast.specialization.model.Specialization;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class DoctorSpecification {

	private DoctorSpecification() {
	}

	public static Specification<Doctor> searchBySearchDto(DoctorSearchDto dto) {
		return (root, query, cb) -> {
			if (query != null) {
				query.distinct(true);
			}
			List<Predicate> predicates = new ArrayList<>();
			Join<Doctor, UserSnapshot> userJoin = root.join("userSnapshot", JoinType.LEFT);
			Join<Doctor, Specialization> specJoin = root.join("specializations", JoinType.LEFT);
			Join<Doctor, Clinic> clinicJoin = root.join("clinics", JoinType.LEFT);

			if (dto != null && dto.query() != null && !dto.query().isBlank()) {
				String pattern = "%" + dto.query().toLowerCase(Locale.ROOT).trim() + "%";
				predicates.add(cb.or(cb.like(cb.lower(userJoin.get("firstName")), pattern),
						cb.like(cb.lower(userJoin.get("lastName")), pattern),
						cb.like(cb.lower(specJoin.get("name")), pattern)));
			}
			if (dto != null && dto.specializationIds() != null && !dto.specializationIds().isEmpty()) {
				predicates.add(specJoin.get("id").in(dto.specializationIds()));
			}
			if (dto != null && dto.clinicIds() != null && !dto.clinicIds().isEmpty()) {
				predicates.add(clinicJoin.get("id").in(dto.clinicIds()));
			}
			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}

}