package com.ewe.form;

import java.util.List;
import java.util.Map;

public class SiteDetailsForm {
	
	private String siteName;
	private String managerName;
	private String managerEmail;
	private String managerPhone;
	private long ownerOrgId;
	private long ownerId;
	private String Address;
	private String latitude;
	private String longitude;
	private Boolean parking;
	private Boolean wifi;
	private Boolean food;
	private Boolean restrooms;
	private String openingTime;
	private String closeTime;
	private String siteStatus;
	private String timezone;
	private long siteId;
	
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerEmail() {
		return managerEmail;
	}
	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}
	public String getManagerPhone() {
		return managerPhone;
	}
	public void setManagerPhone(String managerPhone) {
		this.managerPhone = managerPhone;
	}
	public long getOwnerOrgId() {
		return ownerOrgId;
	}
	public void setOwnerOrgId(long ownerOrgId) {
		this.ownerOrgId = ownerOrgId;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public Boolean getParking() {
		return parking;
	}
	public void setParking(Boolean parking) {
		this.parking = parking;
	}
	public Boolean getWifi() {
		return wifi;
	}
	public void setWifi(Boolean wifi) {
		this.wifi = wifi;
	}
	public Boolean getFood() {
		return food;
	}
	public void setFood(Boolean food) {
		this.food = food;
	}
	public Boolean getRestrooms() {
		return restrooms;
	}
	public void setRestrooms(Boolean restrooms) {
		this.restrooms = restrooms;
	}
	public String getOpeningTime() {
		return openingTime;
	}
	public void setOpeningTime(String openingTime) {
		this.openingTime = openingTime;
	}
	public String getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	public String getSiteStatus() {
		return siteStatus;
	}
	public void setSiteStatus(String siteStatus) {
		this.siteStatus = siteStatus;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	private List<Map<String, Object>> location;

	public List<Map<String, Object>> getLocation() {
	    return location;
	}

	public void setLocation(List<Map<String, Object>> location) {
	    this.location = location;
	}

}
