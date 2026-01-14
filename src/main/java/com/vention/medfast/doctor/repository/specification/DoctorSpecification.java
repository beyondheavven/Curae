package com.vention.medfast.doctor.repository.specification;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.model.Clinic_;
import com.vention.medfast.doctor.dto.DoctorSearchDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.Doctor_;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.model.Specialization_;
import com.vention.medfast.user.model.User;
import com.vention.medfast.user.model.User_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@NoArgsConstructor(access = PRIVATE)
public class DoctorSpecification {

	public static Specification<Doctor> searchBySearchDto(DoctorSearchDto dto) {
		return (root, query, builder) -> {
			List<Predicate> conditions = new ArrayList<>();

			Join<Doctor, User> userJoin = root.join(Doctor_.user, JoinType.LEFT);
			Join<Doctor, Specialization> specializationJoin = root.join(Doctor_.specializations, JoinType.LEFT);
			Join<Doctor, Clinic> clinicJoin = root.join(Doctor_.clinics, JoinType.LEFT);

			if (isNotBlank(dto.query())) {
				conditions.add(createFtsPredicate(builder, dto.query(), List.of(userJoin.get(User_.firstName),
						userJoin.get(User_.lastName), specializationJoin.get(Specialization_.name))));
			}

			if (!isEmpty(dto.specializationIds())) {
				conditions.add(specializationJoin.get(Specialization_.id).in(dto.specializationIds()));
			}

			if (!isEmpty(dto.clinicIds())) {
				conditions.add(clinicJoin.get(Clinic_.id).in(dto.clinicIds()));
			}

			return builder.and(conditions.toArray(Predicate[]::new));
		};
	}

	private static Predicate createFtsPredicate(CriteriaBuilder cb, String searchText,
			List<Expression<String>> columns) {
		String searchQuery = "%" + searchText.toLowerCase() + "%";
		return cb.or(columns.stream().map(column -> cb.like(cb.lower(column), searchQuery)).toArray(Predicate[]::new));
	}

}
