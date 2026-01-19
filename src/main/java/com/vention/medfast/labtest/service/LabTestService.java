package com.vention.medfast.labtest.service;

import com.vention.medfast.labtest.model.LabTest;
import java.util.List;
import java.util.Set;

public interface LabTestService {

	List<LabTest> findAllById(Set<Long> labTestIds);

}
