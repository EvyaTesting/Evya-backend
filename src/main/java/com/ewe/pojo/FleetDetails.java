package com.ewe.pojo;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;

@Entity
@Table (name = "fleet_details")
public class FleetDetails extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String fleetName;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private String baseLocation;
    private String status;
    private Date createdDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = true, updatable = false)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
	private Set<Fleet_Vehicle> fleetVehicle = new HashSet<>();
    @OneToMany(mappedBy = "fleetDetails", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    public Set<Fleet_Vehicle> getFleetVehicle() {
    	return fleetVehicle;
    }
    public void setFleetVehicle(Set<Fleet_Vehicle> fleetVehicle) {
    	this.fleetVehicle = fleetVehicle;
    }
	public String getFleetName() {
		return fleetName;
	}
	public void setFleetName(String fleetName) {
		this.fleetName = fleetName;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	public String getOwnerPhone() {
		return ownerPhone;
	}
	public void setOwnerPhone(String ownerPhone) {
		this.ownerPhone = ownerPhone;
	}
	public String getBaseLocation() {
		return baseLocation;
	}
	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}