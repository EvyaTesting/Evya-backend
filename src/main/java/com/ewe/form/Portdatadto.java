package com.ewe.form;



public class Portdatadto {
	public long getPortId() {
		return PortId;
	}
	public void setPortId(long portId) {
		PortId = portId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getPower_type() {
		return power_type;
	}
	public void setPower_type(String power_type) {
		this.power_type = power_type;
	}
	
	public long getConnectorId() {
		return connectorId;
	}
	public void setConnectorId(long connectorId) {
		this.connectorId = connectorId;
	}
	private long PortId;
	private String status;
	private String type;
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getCapacity() {
		return capacity;
	}
	public void setCapacity(Double capacity) {
		this.capacity = capacity;
	}
	private Double price;
	private String  power_type;
	private Double capacity;
	private long  connectorId;

}
