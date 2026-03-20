package com.ewe.form;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ReportDto {
    private String siteName;
    private String stationName;
    private String portName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Double getKwConsumption() {
		return kwConsumption;
	}

	public void setKwConsumption(Double kwConsumption) {
		this.kwConsumption = kwConsumption;
	}

	public Double getEnergyDelivered() {
		return energyDelivered;
	}

	public void setEnergyDelivered(Double energyDelivered) {
		this.energyDelivered = energyDelivered;
	}

	public Double getRevenue() {
		return revenue;
	}

	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private Double kwConsumption;
    private Double energyDelivered;
    private Double revenue;
    private String location;

    // Constructors, getters, and setters
    public ReportDto() {}

    public ReportDto(String siteName, String stationName, String portName, 
                    LocalDateTime startTime, LocalDateTime endTime, 
                    Double kwConsumption, Double energyDelivered, 
                    Double revenue, String location) {
        this.siteName = siteName;
        this.stationName = stationName;
        this.portName = portName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.kwConsumption = kwConsumption;
        this.energyDelivered = energyDelivered;
        this.revenue = revenue;
        this.location = location;
    }

    // Getters and setters for all fields
    // (Can use Lombok @Data if available)
}