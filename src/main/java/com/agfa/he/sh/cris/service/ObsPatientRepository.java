package com.agfa.he.sh.cris.service;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.agfa.he.sh.cris.domain.EmbeddableBaseData;
import com.agfa.he.sh.cris.domain.ObsPatient;

public interface ObsPatientRepository extends CrudRepository<ObsPatient, String> {
	
	@Query("select p from ObsPatient p where p.patientId = ?1 ")
	List<ObsPatient> findPatientsByPid(String pid);
	
	@Modifying
	@Query(value="update ObsPatient p set p.name=?2, p.pinyin=?3, p.address=?4, p.phone=?5, p.idNumber=?6, p.idNumberType=?7 where p.patientId=?1 ")
	void bulkUpdatePatientByPid(String pid, String name, String pinyin, String address, String phone, String idNumber, EmbeddableBaseData idNumberType);
}
