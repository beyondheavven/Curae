package com.vention.medfast.purchase.service;

import com.vention.medfast.purchase.model.Purchase;
import com.vention.medfast.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

	private final PurchaseRepository purchaseRepository;

	@Override
	public void save(Purchase purchase) {
		purchaseRepository.save(purchase);
	}

	@Override
	public Optional<Purchase> findByStripePaymentIntentId(String id) {
		return purchaseRepository.findByStripePaymentIntentId(id);
	}

}
