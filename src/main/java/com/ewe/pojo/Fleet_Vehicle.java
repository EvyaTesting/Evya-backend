package com.ewe.pojo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "fleet_vehicle")
public class Fleet_Vehicle extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private String model;
    private Double capacityKw;
    private String driver;
    private String location;
    private Double batteryLeft;
    private Integer bookings;
    private String status;
    private String vehicleNumber;
    
    private FleetDetails fleetDetails;
    @ManyToOne(fetch =  FetchType.LAZY, targetEntity = FleetDetails.class)
    @JoinColumn(name = "fleet_id")
    @JsonIgnore
    public FleetDetails getFleetDetails() {
    	return this.fleetDetails;
    }
    public void setFleetDetails(FleetDetails fleetDetails) {
    	this.fleetDetails = fleetDetails;
    }

    public String getModel() {
        return model;
    }
    public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public void setModel(String model) {
        this.model = model;
    }
    public Double getCapacityKw() {
        return capacityKw;
    }
    public void setCapacityKw(Double capacityKw) {
        this.capacityKw = capacityKw;
    }
    public String getDriver() {
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Double getBatteryLeft() {
        return batteryLeft;
    }
    public void setBatteryLeft(Double batteryLeft) {
        this.batteryLeft = batteryLeft;
    }
    public Integer getBookings() {
        return bookings;
    }
    public void setBookings(Integer bookings) {
        this.bookings = bookings;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}