package com.vention.medfast.purchase.repository;

import com.vention.medfast.purchase.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

	Optional<Purchase> findByStripePaymentIntentId(String stripePaymentIntentId);

}
