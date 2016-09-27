package com.agfa.he.sh.cris.service;

import java.util.List;

import com.agfa.he.sh.cris.domain.MasterPatient;

public interface PatientInfoService {

	void obfuscatePatientInfo(MasterPatient p);	
	
	List<MasterPatient> queryPatientByRange(int start, int end);
	
	int countMasterPatient();
}
