package com.vention.medfast.payment.purchase.repository;

import com.vention.medfast.payment.purchase.dto.StatusStatisticDto;
import com.vention.medfast.payment.purchase.model.Purchase;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

	Optional<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId);

	Page<Purchase> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

	@Query("""
			SELECT new com.vention.medfast.payment.purchase.dto.StatusStatisticDto(p.status, COUNT(p), SUM(p.amount))
			FROM Purchase p
			WHERE p.createdAt BETWEEN :from AND :to
			GROUP BY p.status
			""")
	List<StatusStatisticDto> getStatisticsByStatus(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
