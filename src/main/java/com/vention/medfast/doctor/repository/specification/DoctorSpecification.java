package com.vention.medfast.doctor.repository.specification;

import static lombok.AccessLevel.PRIVATE;

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

import java.util.List;

@NoArgsConstructor(access = PRIVATE)
public class DoctorSpecification {

	public static Specification<Doctor> searchDoctor(String searchText) {
		return (root, query, cb) -> {
			if (searchText.isBlank())
				return cb.conjunction();

			Join<Doctor, Specialization> specialization = root.join(Doctor_.SPECIALIZATIONS, JoinType.LEFT);

			Join<Doctor, User> user = root.join(Doctor_.USER, JoinType.LEFT);

			return createFtsPredicate(cb, searchText, List.of(user.get(User_.FIRST_NAME), user.get(User_.LAST_NAME),
					specialization.get(Specialization_.NAME)));

		};

	}

	private static Predicate createFtsPredicate(CriteriaBuilder cb, String searchText,
			List<Expression<String>> columns) {
		String searchQuery = "%" + searchText.toLowerCase() + "%";
		return cb.or(columns.stream().map(column -> cb.like(cb.lower(column), searchQuery)).toArray(Predicate[]::new));
	}

}
