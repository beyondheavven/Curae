package com.vention.medfast.purchase.repository;

import com.vention.medfast.purchase.dto.StatusStatisticDto;
import com.vention.medfast.purchase.model.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

	Optional<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId);

	Page<Purchase> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

	@Query("""
			    SELECT p.status, COUNT(p), SUM(p.amount)
			    FROM Purchase p
			    WHERE p.createdAt BETWEEN :from AND :to
			    GROUP BY p.status
			""")
	List<StatusStatisticDto> getStatisticsByStatus(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
