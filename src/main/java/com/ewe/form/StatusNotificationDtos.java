package com.ewe.form;

import java.util.Date;

public class StatusNotificationDtos {
	public StatusNotificationDtos(Long statusId, String status, Date lastContactedTime) {
		super();
		this.statusId = statusId;
		this.status = status;
		this.lastContactedTime = lastContactedTime;
	}
	
	public Long getStatusId() {
		return statusId;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
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
	private Long statusId;
    private String status;
    private Date lastContactedTime;
}
