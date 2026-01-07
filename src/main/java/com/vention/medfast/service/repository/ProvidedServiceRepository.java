package com.vention.medfast.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vention.medfast.service.model.ProvidedService;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvidedServiceRepository
		extends JpaRepository<ProvidedService, Long>, JpaSpecificationExecutor<ProvidedService> {

}
