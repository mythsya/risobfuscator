package com.agfa.he.sh.cris.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.AttributeOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="act_obs_patient")
public class ObsPatient implements Serializable{

	private static final long serialVersionUID = -5477156984657089389L;

	@Id
	@Column(name = "id", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	@Column(name = "p_uuid")
	private String patientId;

	@Column(name = "p_name")
	private String name;
	
	@Column(name = "p_pinyin")
	private String pinyin;	
	
	@Column(name = "p_home_address")
	private String address;
	
	@Column(name = "p_idnumber")
	private String idNumber;
	
	@Embedded
	@AttributeOverrides(value = { @AttributeOverride(column = @Column(name = "p_idnumber_type_id"), name = "id"),
			@AttributeOverride(column = @Column(name = "p_idnumber_type_code"), name = "bizId"),
			@AttributeOverride(column = @Column(name = "p_idnumber_type_name"), name = "name"),
			@AttributeOverride(column = @Column(name = "p_idnumber_type_category"), name = "category") })
	private EmbeddableBaseData idNumberType;
	
	@Column(name = "p_phone")
	private String phone;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public EmbeddableBaseData getIdNumberType() {
		return idNumberType;
	}

	public void setIdNumberType(EmbeddableBaseData idNumberType) {
		this.idNumberType = idNumberType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}