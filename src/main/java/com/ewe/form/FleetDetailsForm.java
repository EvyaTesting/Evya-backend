package com.ewe.form;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Temporal;


public class FleetDetailsForm {
    private String fleetName;   
    private Long orgId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private String baseLocation;
    private String location;        
    private int totalVehicles;      
    private String vehicleNumber;
    private String model; 
    private Integer bookings;
    private Double capacityKw;
    private String driver;
    private Double batteryLeft;
    private String status;          
    private List<Long> vehicleIds;

    public Integer getBookings() {
		return bookings;
	}

	public void setBookings(Integer bookings) {
		this.bookings = bookings;
	}

	public Double getCapacityKw() {
		return capacityKw;
	}

	public void setCapacityKw(Double capacityKw) {
		this.capacityKw = capacityKw;
	}

	public FleetDetailsForm() {}

    // Getters and Setters
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(int totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Double getBatteryLeft() {
        return batteryLeft;
    }

    public void setBatteryLeft(Double batteryLeft) {
        this.batteryLeft = batteryLeft;
    }

    public String getStatus() {
        return status;
    }

    public String getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(List<Long> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

	public Long getOrgId() {
		return orgId;
	}

}