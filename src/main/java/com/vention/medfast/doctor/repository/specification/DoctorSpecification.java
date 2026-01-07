package com.vention.medfast.doctor.repository.specification;

import static lombok.AccessLevel.PRIVATE;

import com.vention.medfast.doctor.model.Doctor;
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

			Join<Object, Object> specialization = root.join("specializations", JoinType.LEFT);

			Join<Object, Object> user = root.join("user", JoinType.LEFT);

			return createFtsPredicate(cb, searchText,
					List.of(user.get("firstName"), user.get("lastName"), specialization.get("name")));

		};

	}

	private static Predicate createFtsPredicate(CriteriaBuilder cb, String searchText,
			List<Expression<String>> columns) {
		String searchQuery = "%" + searchText.toLowerCase() + "%";
		return cb.or(columns.stream().map(column -> cb.like(cb.lower(column), searchQuery)).toArray(Predicate[]::new));
	}

}
