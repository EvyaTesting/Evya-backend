package com.ewe.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "device_details")
public class DeviceDetails extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private String deviceName;
	private String deviceType;
	private String deviceVersion;
	private String deviceToken;
	private String appVersion;
	private long userId;
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceVersion() {
		return deviceVersion;
	}
	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getDeviceName() {
		return deviceName;
	}

	@Override
    public String toString() {
        return "DeviceDetails [deviceName=" + deviceName + ", deviceType=" + deviceType + ", deviceVersion=" + deviceVersion
                + ", deviceToken=" + deviceToken + ", appVersion=" + appVersion + ", userId=" + userId + "]";
	}
}
