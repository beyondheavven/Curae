package com.vention.medfast.country.repository;

import com.vention.medfast.country.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

	boolean existsByCitizenshipIgnoreCase(String citizenship);

	@Query("""
			    select c.zipRegex as zipRegex
			    from Country c
			    where lower(trim(c.name)) = lower(trim(:countryName))
			""")
	Optional<CountryZipRegexProjection> findZipRegexByCountryName(@Param("countryName") String countryName);

}
