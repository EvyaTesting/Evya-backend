package com.ewe.pojo;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "chargingActivity")
public class ChargingActivity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	private String sitename;
	private String stationName;
	private int connectorId;
	private double portCapacity;
	private String user_email;
	private String phonenumber;
	private String location;
	private long stationId;
	private long portId;
	private long siteId;
	private long ownerId;
	private long userId;
	private String sessionId;
	private long transcationId;
	private double kwConsuption;
	private double energyDelivered;
	private double durationInSec;
	private double revenue;
	private double endSOC;
	private double startSOC;
	private LocalDateTime startTime;
    private LocalDateTime endTime;
    
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
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	public int getConnectorId() {
		return connectorId;
	}
	public void setConnectorId(int connectorId) {
		this.connectorId = connectorId;
	}	
	public double getPortCapacity() {
		return portCapacity;
	}
	public void setPortCapacity(double portCapacity) {
		this.portCapacity = portCapacity;
	}
	public double getKwConsuption() {
		return kwConsuption;
	}
	public void setKwConsuption(double kwConsuption) {
		this.kwConsuption = kwConsuption;
	}
	public double getEnergyDelivered() {
		return energyDelivered;
	}
	public void setEnergyDelivered(double energyDelivered) {
		this.energyDelivered = energyDelivered;
	}
	public double getDurationInSec() {
		return durationInSec;
	}
	public void setDurationInSec(double durationInSec) {
		this.durationInSec = durationInSec;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public long getStationId() {
		return stationId;
	}
	public void setStationId(long stationId) {
		this.stationId = stationId;
	}
	public long getPortId() {
		return portId;
	}
	public void setPortId(long portId) {
		this.portId = portId;
	}
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public long getTranscationId() {
		return transcationId;
	}
	public void setTranscationId(long transcationId) {
		this.transcationId = transcationId;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public double getRevenue() {
		return revenue;
	}
	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}
	public double getEndSOC() {
		return endSOC;
	}
	public void setEndSOC(double endSOC) {
		this.endSOC = endSOC;
	}
	public double getStartSOC() {
		return startSOC;
	}
	public void setStartSOC(double startSOC) {
		this.startSOC = startSOC;
	}	
}
