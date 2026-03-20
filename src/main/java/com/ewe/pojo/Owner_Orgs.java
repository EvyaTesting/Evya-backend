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
@Table(name = "owner_orgs", uniqueConstraints = @UniqueConstraint(columnNames = "orgName"))
public class Owner_Orgs extends BaseEntity{
	
	private static final long serialVersionUID = 1L;
	private String orgName;
	private long whitelabelId;
	private boolean driverFranchise;
	
	private Set<RequestedFranchises> reqFranchises = new HashSet<RequestedFranchises>(0);
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "owner_orgs", orphanRemoval = true)
	@JsonIgnore
	public Set<RequestedFranchises> getReqFranchises() {
		return reqFranchises;
	}
	public void setReqFranchises(Set<RequestedFranchises> reqFranchises) {
		this.reqFranchises = reqFranchises;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public long getWhitelabelId() {
		return whitelabelId;
	}
	public void setWhitelabelId(long whitelabelId) {
		this.whitelabelId = whitelabelId;
	}
	public boolean isDriverFranchise() {
		return driverFranchise;
	}
	public void setDriverFranchise(boolean driverFranchise) {
		this.driverFranchise = driverFranchise;
	}
	
}
