package com.ewe.pojo;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "requested_franchises")
public class RequestedFranchises extends BaseEntity {

    private static final long serialVersionUID = 1L;
  
    private String category;
    private String franchiseName;
    private String sitename;
    private String stationName;
    private String address;
    private String latitude;
    private String longitude;
    private Double chargerCapacity;
    private String mobileNumber;
    private String email;
    private boolean status;
        
    private Owner_Orgs owner_orgs;
    private Site site;
    private User requestedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }
    
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Owner_Orgs.class)
    @JoinColumn (name = "owner_org_id")
    public Owner_Orgs getOwner_orgs() {
		return owner_orgs;
	}
	public void setOwner_orgs(Owner_Orgs owner_orgs) {
		this.owner_orgs = owner_orgs;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Site.class)
	@JoinColumn(name = "site_id")	
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}    
    public String getCategory() {
    	return category;
    }    
	public void setCategory(String category) {
		this.category = category;
	}    
	public String getFranchiseName() {
		return franchiseName;
	}
	public void setFranchiseName(String franchiseName) {
		this.franchiseName = franchiseName;
	}
	public String getSitename() {
		return sitename;
	}
	public void setSitename(String sitename) {
		this.sitename = sitename;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public Double getChargerCapacity() {
		return chargerCapacity;
	}
	public void setChargerCapacity(Double chargerCapacity) {
		this.chargerCapacity = chargerCapacity;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}   
}

//28-10-2025 zip file