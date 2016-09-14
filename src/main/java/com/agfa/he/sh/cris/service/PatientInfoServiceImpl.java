package com.agfa.he.sh.cris.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.agfa.he.sh.cris.domain.MasterPatient;

@Component
@Transactional(readOnly=true)
public class PatientInfoServiceImpl implements PatientInfoService{

	private final static Logger logger = LoggerFactory.getLogger(PatientInfoServiceImpl.class);
	
	@Autowired
	private MasterPatientRepository masterPatientRepository;
	
	@Autowired
	private ObsPatientRepository obsPatientRepository;
	
	private TaskPatientRepository taskPatientRepository;
	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void obfuscatePatientInfo(MasterPatient p) {
		
		p.setAddress(null);
		p.setIdNumber(null);
		p.setIdNumberType(null);
	}

}
