package com.vention.medfast.purchase.service;

import com.vention.medfast.purchase.model.Purchase;

import java.util.Optional;

public interface PurchaseService {

	void save(Purchase purchase);

	Optional<Purchase> findByStripePaymentIntentId(String id);

}
