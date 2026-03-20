package com.ewe.form;


import java.util.List;

public class PortDto {
	public long getStationId() {
		return stationId;
	}
	public void setStationId(long stationId) {
		this.stationId = stationId;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public List<Portdatadto> getPortdata() {
		return portdata;
	}
	public void setPortdata(List<Portdatadto> portdata) {
		this.portdata = portdata;
	}
	private long stationId;
	private String stationName;
	private List<Portdatadto> portdata;
}
