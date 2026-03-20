package com.ewe.pojo;

import java.util.HashSet;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "site", uniqueConstraints = @UniqueConstraint(columnNames = "siteName"))
public class Site  extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private String siteName;
	private String managerName;
	private String managerEmail;
	private String managerPhone;
	private long ownerOrg;
	private long ownerId;
	
	private Set<SiteLocationDetails> location = new HashSet<SiteLocationDetails>(0);
	private Set<SiteFacilities> facilities = new HashSet<SiteFacilities>(0);
	private Set<SiteOperationalDetails> operations = new HashSet<SiteOperationalDetails>(0);
	private Set<Station> station = new HashSet<Station>(0);
	
	private Set<RequestedFranchises> reqFranchises = new HashSet<RequestedFranchises>(0);
	
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
	public long getOwnerOrg() {
		return ownerOrg;
	}
	public void setOwnerOrg(long ownerOrg) {
		this.ownerOrg = ownerOrg;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
    
	@OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	public Set<SiteLocationDetails> getLocation() {
		return location;
	}
	public void setLocation(Set<SiteLocationDetails> location) {
		this.location = location;
	}
	
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    public Set<SiteFacilities> getFacilities() {
		return facilities;
	}
	public void setFacilities(Set<SiteFacilities> facilities) {
		this.facilities = facilities;
	}
	
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    public Set<SiteOperationalDetails> getOperations() {
		return operations;
	}
	public void setOperations(Set<SiteOperationalDetails> operations) {
		this.operations = operations;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "site",orphanRemoval = true)
	@JsonIgnore
	public Set<Station> getStation() {
		return station;
	}
	public void setStation(Set<Station> station) {
		this.station = station;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "site", orphanRemoval = true)
	@JsonIgnore
	public Set<RequestedFranchises> getReqFranchises() {
		return reqFranchises;
	}
	public void setReqFranchises(Set<RequestedFranchises> reqFranchises) {
		this.reqFranchises = reqFranchises;
	}
}
