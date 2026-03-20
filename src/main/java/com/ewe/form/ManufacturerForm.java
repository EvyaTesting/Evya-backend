package com.ewe.form;

import java.util.List;

public class ManufacturerForm {
    private String manufacturerName;
    private String country;
    private String contactInfo;
    private String mobileNumber;
    private List<ChargerForm> chargers;
    
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
	public List<ChargerForm> getChargers() {
		return chargers;
	}
	public void setChargers(List<ChargerForm> chargers) {
		this.chargers = chargers;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

   
}