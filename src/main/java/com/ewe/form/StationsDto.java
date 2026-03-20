package com.ewe.form;

import java.util.Date;
import java.util.List;

public class StationsDto {
	private Long stationId;
    private String stationName;
    private String serialNumber;
    private String currentType;
    private Date lastHeartbeat;
    private String status;
    public List<PortsDto> getPorts() {
		return ports;
	}
	public void setPorts(List<PortsDto> ports) {
		this.ports = ports;
	}
	private List<PortsDto> ports;
	public Long getStationId() {
		return stationId;
	}
	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getCurrentType() {
		return currentType;
	}
	public void setCurrentType(String currentType) {
		this.currentType = currentType;
	}
	public Date getLastHeartbeat() {
		return lastHeartbeat;
	}
	public void setLastHeartbeat(Date lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
