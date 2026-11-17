package com.vention.medfast.payment.catalog.repository;

import com.vention.medfast.payment.catalog.model.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {

}
