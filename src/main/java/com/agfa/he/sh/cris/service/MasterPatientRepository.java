package com.agfa.he.sh.cris.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.agfa.he.sh.cris.domain.MasterPatient;

public interface MasterPatientRepository extends Repository<MasterPatient, String>{

	@Query("select t from MasterPatient t")
	Page<MasterPatient> listPatients(Pageable pageable);
	
	@Query(value="select count(1) from role_patient", nativeQuery=true)
	Long countMasterPatient();
}
