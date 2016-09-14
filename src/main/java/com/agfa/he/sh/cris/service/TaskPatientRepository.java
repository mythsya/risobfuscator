package com.agfa.he.sh.cris.service;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.agfa.he.sh.cris.domain.ObsPatient;
import com.agfa.he.sh.cris.domain.TaskPatient;

public interface TaskPatientRepository extends Repository<TaskPatient, String> {

	@Query("select p from TaskPatient p where p.patientId = ?1 ")
	List<ObsPatient> findPatientsByPid(String pid);
	
	@Modifying
	@Query(value="update TaskPatient p set p.name=?2, p.pinyin=?3, p.phone=?4, p.idNumber=?5, p.idNumberType=?6 where p.patientId=?1 ")
	void bulkUpdatePatientByPid(String pid, String name, String pinyin, String phone, String idNumber, String idNumberType);
}
