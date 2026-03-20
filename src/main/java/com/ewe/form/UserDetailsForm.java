package com.ewe.form;

import java.util.Map;

public class UserDetailsForm {
	
	private String fullname;
	private String username;
	private String email;
	private String mobileNumber;
	private String rolename;
	
	private String password;
	private String confirmPassword;
	
	private String address;
	private String city;
	private String country;
	private String state;
	private String zipCode;
	
	private String orgName;
	private Long orgId;
	
	private boolean passwordchange;
	
	private Map<String,Object> deviceDetails;
	
	private long userId;
		
	public boolean isPasswordchange() {
		return passwordchange;
	}
	public void setPasswordchange(boolean passwordchange) {
		this.passwordchange = passwordchange;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}	
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public Map<String, Object> getDeviceDetails() {
		return deviceDetails;
	}
	public void setDeviceDetails(Map<String, Object> deviceDetails) {
		this.deviceDetails = deviceDetails;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	
}
