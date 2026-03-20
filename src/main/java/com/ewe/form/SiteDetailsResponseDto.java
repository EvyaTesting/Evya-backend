package com.ewe.form;

import java.util.List;

public class SiteDetailsResponseDto {
    public int getTotalPorts() {
		return totalPorts;
	}
	public void setTotalPorts(int totalPorts) {
		this.totalPorts = totalPorts;
	}
	public int getTotalStations() {
		return totalStations;
	}
	public void setTotalStations(int totalStations) {
		this.totalStations = totalStations;
	}
	public int getAvailablePorts() {
		return availablePorts;
	}
	public void setAvailablePorts(int availablePorts) {
		this.availablePorts = availablePorts;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPowerType() {
		return powerType;
	}
	public void setPowerType(String powerType) {
		this.powerType = powerType;
	}
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public Long getOwnerOrgId() {
		return ownerOrgId;
	}
	public void setOwnerOrgId(Long ownerOrgId) {
		this.ownerOrgId = ownerOrgId;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public List<LocationDto> getLocations() {
		return locations;
	}
	public void setLocations(List<LocationDto> locations) {
		this.locations = locations;
	}
	public List<StationsDto> getStations() {
		return stations;
	}
	public void setStations(List<StationsDto> stations) {
		this.stations = stations;
	}
	private int totalPorts;
    private int totalStations;
    private int availablePorts;
    private String power;
    private String price;
    private String powerType;
    private Long siteId;
    private String siteName;
    private Long ownerOrgId;
    private Long ownerId;
    private List<LocationDto> locations;
    private String managerName;
    private String managerPhone;

    public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerPhone() {
		return managerPhone;
	}
	public void setManagerPhone(String managerPhone) {
		this.managerPhone = managerPhone;
	}
	private List<StationsDto> stations;
}

