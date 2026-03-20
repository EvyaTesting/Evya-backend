package com.ewe.form;

import java.util.List;

public class ChargerForm {
	private Long id;
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private String chargerType;
    private double totalCapacityKW;
    private String currentType;
    private int portQuantity; 
    public String getCurrentType() {
		return currentType;
	}
	public void setCurrentType(String currentType) {
		this.currentType = currentType;
	}
	public int getPortQuantity() {
		return portQuantity;
	}
	public void setPortQuantity(int portQuantity) {
		this.portQuantity = portQuantity;
	}
	private List<PortForm> ports;
    
	public String getChargerType() {
		return chargerType;
	}
	public void setChargerType(String chargerType) {
		this.chargerType = chargerType;
	}
	public double getTotalCapacityKW() {
		return totalCapacityKW;
	}
	public void setTotalCapacityKW(double totalCapacityKW) {
		this.totalCapacityKW = totalCapacityKW;
	}
	public List<PortForm> getPorts() {
		return ports;
	}
	public void setPorts(List<PortForm> ports) {
		this.ports = ports;
	}
	

   
}