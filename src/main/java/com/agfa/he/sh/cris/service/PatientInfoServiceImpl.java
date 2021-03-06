package com.agfa.he.sh.cris.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.agfa.he.sh.common.util.PinyinUtil;
import com.agfa.he.sh.common.util.PseudoRandomUtil;
import com.agfa.he.sh.common.util.PinyinUtil.PinyinCaseType;
import com.agfa.he.sh.cris.domain.MasterPatient;
import com.agfa.he.sh.cris.domain.ObsPatient;

@Component
@Transactional(readOnly=true)
public class PatientInfoServiceImpl implements PatientInfoService{

	private final static Logger logger = LoggerFactory.getLogger(PatientInfoServiceImpl.class);
	
	@Autowired
	private MasterPatientRepository masterPatientRepository;
	
	@Autowired
	private ObsPatientRepository obsPatientRepository;
	
	@Autowired
	private TaskPatientRepository taskPatientRepository;
	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void obfuscatePatientInfo(MasterPatient p) {		
		
		String sexCode = "M";
		if (p.getSex() != null) {
			sexCode = p.getSex().getCode();
		}
		String newName = PseudoRandomUtil.randomName(p.getName(), sexCode);
		String newPy = PinyinUtil.generatePinyinStr(newName, PinyinCaseType.CAPITALIZED, false);
		String newPhone = PseudoRandomUtil.randomPhoneNumber(p.getPhone());
		
		p.setAddress(null);
		p.setIdNumber(null);
		p.setIdNumberType(null);
		p.setName(newName);
		p.setPinyin(newPy);
		p.setPhone(newPhone);
		p = masterPatientRepository.save(p);
		
		obsPatientRepository.bulkUpdatePatientByPid(p.getId(), newName, newPy, null, newPhone, null, null);
		
		taskPatientRepository.bulkUpdatePatientByPid(p.getId(), newName, newPy, newPhone, null, null);
		
		if (logger.isInfoEnabled()) {
			logger.info("Succeeded to update patient info. [patient id="+p.getId()+"] => { name="+newName+", pinyin="+newPy+", phone="+newPhone+" }");
		}
	}


	@Override
	public int countMasterPatient() {
		return masterPatientRepository.countMasterPatient();
	}


	@Override
	public List<MasterPatient> queryPatientByRange(int start, int end) {		
		return masterPatientRepository.queryPatientByRange(start, end);
	}


	

}
