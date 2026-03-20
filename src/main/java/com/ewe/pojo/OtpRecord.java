package com.ewe.pojo;

import java.time.LocalDateTime;

public class OtpRecord {
    private final String otp;
    private final String phoneNumber;
    private final LocalDateTime timestamp;
    private Long userId;

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public OtpRecord(String otp, String phoneNumber, LocalDateTime timestamp,Long userId) {
        this.otp = otp;
        this.phoneNumber = phoneNumber;
        this.timestamp = timestamp;
        this.userId=userId;
    }

    public String getOtp() {
        return otp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
