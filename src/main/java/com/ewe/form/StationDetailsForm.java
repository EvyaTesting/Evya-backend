package com.ewe.form;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StationDetailsForm {
	
	private String stationName;
    @JsonProperty("OCPPId")
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
	private long siteId;
	private long chargerdetailsId;
	
	private long connectorId1;
	private String connectorName1;
	private String connector_type1;
	private String power_type1;
	private Double voltage_rating1;
	private Double current_rating1;
	private Double max_power_kW1;
	private String billingUnits1;
	private Double billingAmount1;
	private long connectorDetailsID1;
	
	private long connectorId2;
	private String connectorName2;
	private String connector_type2;
	private String power_type2;
	private Double voltage_rating2;
	private Double current_rating2;
	private Double max_power_kW2;
	private String billingUnits2;
	private Double billingAmount2;
	private long connectorDetailsID2;
	
	private long connectorId3;
	private String connectorName3;
	private String connector_type3;
	private String power_type3;
	private Double voltage_rating3;
	private Double current_rating3;
	private Double max_power_kW3;
	private String billingUnits3;
	private Double billingAmount3;
	private long connectorDetailsID3;
	
	private long stationId;
	
	
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
		OCPPId = oCPPId;
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
		V2G_support = v2g_support;
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
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	public long getChargerdetailsId() {
		return chargerdetailsId;
	}
	public void setChargerdetailsId(long chargerdetailsId) {
		this.chargerdetailsId = chargerdetailsId;
	}
	public long getConnectorId1() {
		return connectorId1;
	}
	public void setConnectorId1(long connectorId1) {
		this.connectorId1 = connectorId1;
	}
	public String getConnectorName1() {
		return connectorName1;
	}
	public void setConnectorName1(String connectorName1) {
		this.connectorName1 = connectorName1;
	}
	public String getConnector_type1() {
		return connector_type1;
	}
	public void setConnector_type1(String connector_type1) {
		this.connector_type1 = connector_type1;
	}
	public String getPower_type1() {
		return power_type1;
	}
	public void setPower_type1(String power_type1) {
		this.power_type1 = power_type1;
	}
	public Double getVoltage_rating1() {
		return voltage_rating1;
	}
	public void setVoltage_rating1(Double voltage_rating1) {
		this.voltage_rating1 = voltage_rating1;
	}
	public Double getCurrent_rating1() {
		return current_rating1;
	}
	public void setCurrent_rating1(Double current_rating1) {
		this.current_rating1 = current_rating1;
	}
	public Double getMax_power_kW1() {
		return max_power_kW1;
	}
	public void setMax_power_kW1(Double max_power_kW1) {
		this.max_power_kW1 = max_power_kW1;
	}
	public String getBillingUnits1() {
		return billingUnits1;
	}
	public void setBillingUnits1(String billingUnits1) {
		this.billingUnits1 = billingUnits1;
	}
	public Double getBillingAmount1() {
		return billingAmount1;
	}
	public void setBillingAmount1(Double billingAmount1) {
		this.billingAmount1 = billingAmount1;
	}
	public long getConnectorDetailsID1() {
		return connectorDetailsID1;
	}
	public void setConnectorDetailsID1(long connectorDetailsID1) {
		this.connectorDetailsID1 = connectorDetailsID1;
	}
	public long getConnectorId2() {
		return connectorId2;
	}
	public void setConnectorId2(long connectorId2) {
		this.connectorId2 = connectorId2;
	}
	public String getConnectorName2() {
		return connectorName2;
	}
	public void setConnectorName2(String connectorName2) {
		this.connectorName2 = connectorName2;
	}
	public String getConnector_type2() {
		return connector_type2;
	}
	public void setConnector_type2(String connector_type2) {
		this.connector_type2 = connector_type2;
	}
	public String getPower_type2() {
		return power_type2;
	}
	public void setPower_type2(String power_type2) {
		this.power_type2 = power_type2;
	}
	public Double getVoltage_rating2() {
		return voltage_rating2;
	}
	public void setVoltage_rating2(Double voltage_rating2) {
		this.voltage_rating2 = voltage_rating2;
	}
	public Double getCurrent_rating2() {
		return current_rating2;
	}
	public void setCurrent_rating2(Double current_rating2) {
		this.current_rating2 = current_rating2;
	}
	public Double getMax_power_kW2() {
		return max_power_kW2;
	}
	public void setMax_power_kW2(Double max_power_kW2) {
		this.max_power_kW2 = max_power_kW2;
	}
	public String getBillingUnits2() {
		return billingUnits2;
	}
	public void setBillingUnits2(String billingUnits2) {
		this.billingUnits2 = billingUnits2;
	}
	public Double getBillingAmount2() {
		return billingAmount2;
	}
	public void setBillingAmount2(Double billingAmount2) {
		this.billingAmount2 = billingAmount2;
	}
	public long getConnectorDetailsID2() {
		return connectorDetailsID2;
	}
	public void setConnectorDetailsID2(long connectorDetailsID2) {
		this.connectorDetailsID2 = connectorDetailsID2;
	}
	public long getConnectorId3() {
		return connectorId3;
	}
	public void setConnectorId3(long connectorId3) {
		this.connectorId3 = connectorId3;
	}
	public String getConnectorName3() {
		return connectorName3;
	}
	public void setConnectorName3(String connectorName3) {
		this.connectorName3 = connectorName3;
	}
	public String getConnector_type3() {
		return connector_type3;
	}
	public void setConnector_type3(String connector_type3) {
		this.connector_type3 = connector_type3;
	}
	public String getPower_type3() {
		return power_type3;
	}
	public void setPower_type3(String power_type3) {
		this.power_type3 = power_type3;
	}
	public Double getVoltage_rating3() {
		return voltage_rating3;
	}
	public void setVoltage_rating3(Double voltage_rating3) {
		this.voltage_rating3 = voltage_rating3;
	}
	public Double getCurrent_rating3() {
		return current_rating3;
	}
	public void setCurrent_rating3(Double current_rating3) {
		this.current_rating3 = current_rating3;
	}
	public Double getMax_power_kW3() {
		return max_power_kW3;
	}
	public void setMax_power_kW3(Double max_power_kW3) {
		this.max_power_kW3 = max_power_kW3;
	}
	public String getBillingUnits3() {
		return billingUnits3;
	}
	public void setBillingUnits3(String billingUnits3) {
		this.billingUnits3 = billingUnits3;
	}
	public Double getBillingAmount3() {
		return billingAmount3;
	}
	public void setBillingAmount3(Double billingAmount3) {
		this.billingAmount3 = billingAmount3;
	}
	public long getConnectorDetailsID3() {
		return connectorDetailsID3;
	}
	public void setConnectorDetailsID3(long connectorDetailsID3) {
		this.connectorDetailsID3 = connectorDetailsID3;
	}
}
