package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.dto.TestHistoryRow;
import com.vention.medfast.appointment.model.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

	@Query("""
			    select new com.vention.medfast.appointment.dto.TestHistoryRow(
			        t.id,
			        t.date,
			        t.status
			    )
			    from Test t
			    where t.patient.user.id = :userId
			    order by t.date desc
			""")
	Page<TestHistoryRow> findLabHistoryRows(@Param("userId") Long userId, Pageable pageable);

}
