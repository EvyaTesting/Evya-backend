package com.ewe.pojo;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "statusNotification")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "port" })
public class StatusNotification extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	private long stationId;
	private String status;
	private Date lastContactedTime;
	private Port port;
    private Long connectorId;
	
	public Long getConnectorId() {
		return connectorId;
	}
	public void setConnectorId(Long connectorId) {
		this.connectorId = connectorId;
	}
	public long getStationId() {
		return stationId;
	}
	public void setStationId(long stationId) {
		this.stationId = stationId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getLastContactedTime() {
		return lastContactedTime;
	}
	public void setLastContactedTime(Date lastContactedTime) {
		this.lastContactedTime = lastContactedTime;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Port.class)
	@JoinColumn(name = "port_id")
	public Port getPort() {
		return port;
	}
	public void setPort(Port port) {
		this.port = port;
	}
}