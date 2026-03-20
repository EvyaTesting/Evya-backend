package com.ewe.form;

import java.util.List;

public class PortForm {
	private Long id;
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private String connectorType;
    private double portCapacityKW;
    private double maxInputVoltageV;
    private double maxOutputVoltageV;
    private double outputCurrentA;
    private String portDisplayName;
   
	public String getConnectorType() {
		return connectorType;
	}
	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType;
	}
	public double getPortCapacityKW() {
		return portCapacityKW;
	}
	public void setPortCapacityKW(double portCapacityKW) {
		this.portCapacityKW = portCapacityKW;
	}
	public double getMaxInputVoltageV() {
		return maxInputVoltageV;
	}
	public void setMaxInputVoltageV(double maxInputVoltageV) {
		this.maxInputVoltageV = maxInputVoltageV;
	}
	public double getMaxOutputVoltageV() {
		return maxOutputVoltageV;
	}
	public void setMaxOutputVoltageV(double maxOutputVoltageV) {
		this.maxOutputVoltageV = maxOutputVoltageV;
	}
	public double getOutputCurrentA() {
		return outputCurrentA;
	}
	public void setOutputCurrentA(double outputCurrentA) {
		this.outputCurrentA = outputCurrentA;
	}
	public String getPortDisplayName() {
		return portDisplayName;
	}
	public void setPortDisplayName(String portDisplayName) {
		this.portDisplayName = portDisplayName;
	}

   
}