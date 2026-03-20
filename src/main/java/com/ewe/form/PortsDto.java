package com.ewe.form;

import java.util.List;

public class PortsDto {
	 public Long getPortId() {
		return portId;
	}
	public void setPortId(Long portId) {
		this.portId = portId;
	}
	public Long getConnectorId() {
		return connectorId;
	}
	public void setConnectorId(Long connectorId) {
		this.connectorId = connectorId;
	}
	public String getConnectorName() {
		return connectorName;
	}
	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}
	public String getConnectorType() {
		return connectorType;
	}
	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType;
	}
	public Double getPowerCapacity() {
		return powerCapacity;
	}
	public void setPowerCapacity(Double powerCapacity) {
		this.powerCapacity = powerCapacity;
	}
	public String getPowerType() {
		return powerType;
	}
	public void setPowerType(String powerType) {
		this.powerType = powerType;
	}
	public Double getBillingAmount() {
		return billingAmount;
	}
	public void setBillingAmount(Double billingAmount) {
		this.billingAmount = billingAmount;
	}
	public String getBillingUnits() {
		return billingUnits;
	}
	public void setBillingUnits(String billingUnits) {
		this.billingUnits = billingUnits;
	}
	public List<StatusNotificationDtos> getStatusNotifications() {
		return statusNotifications;
	}
	public void setStatusNotifications(List<StatusNotificationDtos> statusNotifications) {
		this.statusNotifications = statusNotifications;
	}
	private Long portId;
	    private Long connectorId;
	    private String connectorName;
	    private String connectorType;
	    private Double powerCapacity;
	    private String powerType;
	    private Double billingAmount;
	    private String billingUnits;
	    private List<StatusNotificationDtos> statusNotifications;
}
