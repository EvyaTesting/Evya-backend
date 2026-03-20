package com.ewe.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhonePePaymentResponse {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("state")
    private String state;


	@JsonProperty("expireAt")
    private Long expireAt;

    @JsonProperty("redirectUrl")
    private String redirectUrl;

    public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Long expireAt) {
		this.expireAt = expireAt;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}  
}