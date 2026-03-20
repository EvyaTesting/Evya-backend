package com.ewe.pojo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "connector_details")

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "chargingStation", "port"})

public class ConnectorDetails extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
   
    private ChargerDetails chargingStation;
    private String connectorType;
    private Double portCapacityKW;
    private Double maxInputVoltageV;
    private Double maxOutputVoltageV;
    private Double outputCurrentA;
    private String portDisplayName;
    private Set<Port> port = new HashSet<Port>(0);
    
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "connectorDetails")
    public Set<Port> getPort() {
		return port;
	}

	public void setPort(Set<Port> port) {
		this.port = port;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charger_id")
    public ChargerDetails getChargingStation() {
        return chargingStation;
    }
    
    public void setChargingStation(ChargerDetails chargingStation) {
        this.chargingStation = chargingStation;
    }

	public String getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType;
	}

	public Double getPortCapacityKW() {
		return portCapacityKW;
	}

	public void setPortCapacityKW(Double portCapacityKW) {
		this.portCapacityKW = portCapacityKW;
	}

	public Double getMaxInputVoltageV() {
		return maxInputVoltageV;
	}

	public void setMaxInputVoltageV(Double maxInputVoltageV) {
		this.maxInputVoltageV = maxInputVoltageV;
	}

	public Double getMaxOutputVoltageV() {
		return maxOutputVoltageV;
	}

	public void setMaxOutputVoltageV(Double maxOutputVoltageV) {
		this.maxOutputVoltageV = maxOutputVoltageV;
	}

	public Double getOutputCurrentA() {
		return outputCurrentA;
	}

	public void setOutputCurrentA(Double outputCurrentA) {
		this.outputCurrentA = outputCurrentA;
	}

	public String getPortDisplayName() {
		return portDisplayName;
	}

	public void setPortDisplayName(String portDisplayName) {
		this.portDisplayName = portDisplayName;
	}   
}