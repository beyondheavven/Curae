package com.vention.curae.service.repository;

import com.vention.curae.service.model.ProvidedService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvidedServiceRepository
		extends JpaRepository<ProvidedService, Long>, JpaSpecificationExecutor<ProvidedService> {

}