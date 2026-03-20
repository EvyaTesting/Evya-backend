package com.ewe.form;



import java.util.Set;



import java.util.Set;

public class GetSiteDetailsDto {

    private Long siteId;
    private String siteName;
    private String address;

    private long noOfPorts;
    private long avaiPorts;
    

    private String siteStatus;

    private Set<String> powerTypes;
    private Set<String> connectorTypes;
    private Double distanceKm;
    private Double latitude;
    private Double longitude;


    public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(Double distanceKm) {
		this.distanceKm = distanceKm;
	}

	// Combined range fields
    private String capacityRange;   // ex: "3.2-3.2"
    private String priceRange;      // ex: "10-20"

   

    // -------- Getters & Setters --------

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getNoOfPorts() {
        return noOfPorts;
    }

    public void setNoOfPorts(long noOfPorts) {
        this.noOfPorts = noOfPorts;
    }

    public long getAvaiPorts() {
        return avaiPorts;
    }

    public void setAvaiPorts(long avaiPorts) {
        this.avaiPorts = avaiPorts;
    }

   

    public String getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(String siteStatus) {
        this.siteStatus = siteStatus;
    }

    public Set<String> getPowerTypes() {
        return powerTypes;
    }

    public void setPowerTypes(Set<String> powerTypes) {
        this.powerTypes = powerTypes;
    }

    public Set<String> getConnectorTypes() {
        return connectorTypes;
    }

    public void setConnectorTypes(Set<String> connectorTypes) {
        this.connectorTypes = connectorTypes;
    }

    public String getCapacityRange() {
        return capacityRange;
    }

    public void setCapacityRange(String capacityRange) {
        this.capacityRange = capacityRange;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

  
    }


