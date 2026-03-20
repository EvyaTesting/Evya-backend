package com.ewe.pojo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "manufacturer_details")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class ManufacturerDetails extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private String manufacturerName;
	private String country;
	private String contactInfo;
	private String mobileNumber;
	private Set<ChargerDetails> chargingStation = new HashSet<>();

	@JsonProperty
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "manufacturerDetails")
	@Fetch(value = FetchMode.SELECT)
	public Set<ChargerDetails> getChargingStation() {
		return chargingStation;
	}

	public void setChargingStation(Set<ChargerDetails> chargingStation) {
		this.chargingStation = chargingStation;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}	
}