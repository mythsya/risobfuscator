package com.agfa.he.sh.cris.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.agfa.he.sh.cris.domain.MasterPatient;

public interface MasterPatientRepository extends CrudRepository<MasterPatient, String>{

	@Query("select t from MasterPatient t")
	Page<MasterPatient> listPatients(Pageable pageable);
	
	@Query(value="select count(1) from role_patient", nativeQuery=true)
	int countMasterPatient();
	
	@Query(value="select * from (select row_.*, rownum rownum_ from (select p.id, p.addr, p.idnumber, p.idnumbertype_id, p.name, p.homephonenumber, p.pycode, p.administrativegendercode_id from role_patient p order by p.id asc) row_ where rownum <= ?2) where rownum_ >= ?1", nativeQuery=true)
	List<MasterPatient> queryPatientByRange(int start, int end);
}
