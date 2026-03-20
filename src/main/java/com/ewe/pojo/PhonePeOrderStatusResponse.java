package com.ewe.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhonePeOrderStatusResponse {

	@JsonProperty("orderId")
	private String orderId;

	@JsonProperty("state")
	private String state;

	@JsonProperty("amount")
	private Long amount;

	@JsonProperty("merchantOrderId")
	private String merchantOrderId;

	@JsonProperty("expireAt")
	private Long expireAt;

	@JsonProperty("metaInfo")
	private Object metaInfo;

	@JsonProperty("paymentDetails")
	private List<Object> paymentDetails;

	// Getters and Setters
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

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getMerchantOrderId() {
		return merchantOrderId;
	}

	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	public Long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Long expireAt) {
		this.expireAt = expireAt;
	}

	public Object getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(Object metaInfo) {
		this.metaInfo = metaInfo;
	}

	public List<Object> getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(List<Object> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
}