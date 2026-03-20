package com.ewe.pojo;

import java.sql.Date;
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
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "station")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class Station extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String stationName;
    private String OCPPId;
    private String serialNo;
    private String model;
    private long manufacturerId;
    private String firmware_version;
    private String ocppVersion;
    private String communication_method;
    private String stationStatus;
    private Double max_output_power_kW;
    private Double voltage_range;
    private String current_type;
    private long number_of_ports;
    private boolean V2G_support;
    private boolean plug_and_charger;
    private Date lastHeartBeat;    

    private Site site;
    private ChargerDetails chargerdetails;
    private Set<Port> port = new HashSet<>(0);

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getOCPPId() {
        return OCPPId;
    }

    public void setOCPPId(String oCPPId) {
        this.OCPPId = oCPPId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_version = firmware_version;
    }

    public String getOcppVersion() {
        return ocppVersion;
    }

    public void setOcppVersion(String ocppVersion) {
        this.ocppVersion = ocppVersion;
    }

    public String getCommunication_method() {
        return communication_method;
    }

    public void setCommunication_method(String communication_method) {
        this.communication_method = communication_method;
    }

    public String getStationStatus() {
        return stationStatus;
    }

    public void setStationStatus(String stationStatus) {
        this.stationStatus = stationStatus;
    }

    public Double getMax_output_power_kW() {
        return max_output_power_kW;
    }

    public void setMax_output_power_kW(Double max_output_power_kW) {
        this.max_output_power_kW = max_output_power_kW;
    }

    public Double getVoltage_range() {
        return voltage_range;
    }

    public void setVoltage_range(Double voltage_range) {
        this.voltage_range = voltage_range;
    }

    public String getCurrent_type() {
        return current_type;
    }

    public void setCurrent_type(String current_type) {
        this.current_type = current_type;
    }

    public long getNumber_of_ports() {
        return number_of_ports;
    }

    public void setNumber_of_ports(long number_of_ports) {
        this.number_of_ports = number_of_ports;
    }

    public boolean isV2G_support() {
        return V2G_support;
    }

    public void setV2G_support(boolean v2g_support) {
        this.V2G_support = v2g_support;
    }

    public boolean isPlug_and_charger() {
        return plug_and_charger;
    }

    public void setPlug_and_charger(boolean plug_and_charger) {
        this.plug_and_charger = plug_and_charger;
    }

    public Date getLastHeartBeat() {
        return lastHeartBeat;
    }

    public void setLastHeartBeat(Date lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }
    
    // ----- Relationships -----

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id")
    @JsonManagedReference
    public Site getSite() {
        return site;
    }
    

    public void setSite(Site site) {
        this.site = site;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chargerdetails_id")
    @JsonBackReference
    public ChargerDetails getChargerdetails() {
        return chargerdetails;
    }

    public void setChargerdetails(ChargerDetails chargerdetails) {
        this.chargerdetails = chargerdetails;
    }

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    public Set<Port> getPort() {
        return port;
    }

    public void setPort(Set<Port> port) {
        this.port = port;
    }

	
}