package com.vention.medfast.service.repository.specification;

import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.model.ProvidedService_;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ProvidedServiceSpecification {

	public static Specification<ProvidedService> searchService(String searchText) {
		return (root, query, cb) -> {

			if (searchText == null || searchText.isBlank())
				return cb.conjunction();

			return cb.like(cb.lower(root.get(ProvidedService_.name)), "%" + searchText.toLowerCase() + "%");
		};
	}

}
