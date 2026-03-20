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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "port")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "station", "hibernateLazyInitializer", "handler" })
public class Port extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private long connectorId;
    private String connectorName;
    private String connector_type;
    private String power_type;
    private Double voltage_rating;
    private Double current_rating;
    private Double max_power_kW;
    private String billingUnits;
    private Double billingAmount;
    private Station station;
    
    private ConnectorDetails connectorDetails;
    private Set<StatusNotification> statusNotifcation = new HashSet<StatusNotification>(0);

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ConnectorDetails.class)
    @JoinColumn(name = "connectordetails_id")
    @JsonBackReference  // Prevents infinite recursion during serialization
    public ConnectorDetails getConnectorDetails() {
        return connectorDetails;
    }

    public void setConnectorDetails(ConnectorDetails connectorDetails) {
        this.connectorDetails = connectorDetails;
    }

    public long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(long connectorId) {
        this.connectorId = connectorId;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getConnector_type() {
        return connector_type;
    }

    public void setConnector_type(String connector_type) {
        this.connector_type = connector_type;
    }

    public String getPower_type() {
        return power_type;
    }

    public void setPower_type(String power_type) {
        this.power_type = power_type;
    }

    public Double getVoltage_rating() {
        return voltage_rating;
    }

    public void setVoltage_rating(Double voltage_rating) {
        this.voltage_rating = voltage_rating;
    }

    public Double getCurrent_rating() {
        return current_rating;
    }

    public void setCurrent_rating(Double current_rating) {
        this.current_rating = current_rating;
    }

    public Double getMax_power_kW() {
        return max_power_kW;
    }

    public void setMax_power_kW(Double max_power_kW) {
        this.max_power_kW = max_power_kW;
    }

    public String getBillingUnits() {
        return billingUnits;
    }

    public void setBillingUnits(String billingUnits) {
        this.billingUnits = billingUnits;
    }

    public Double getBillingAmount() {
        return billingAmount;
    }

    public void setBillingAmount(Double billingAmount) {
        this.billingAmount = billingAmount;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "port")
    public Set<StatusNotification> getStatusNotifcation() {
        return statusNotifcation;
    }

    public void setStatusNotifcation(Set<StatusNotification> statusNotifcation) {
        this.statusNotifcation = statusNotifcation;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Station.class)
    @JoinColumn(name = "station_id")
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

	

}