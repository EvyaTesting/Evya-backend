package com.ewe.pojo;


public class PhonePePaymentRequest {
    private String merchantOrderId;
    private Long amount;           
    private Integer expireAfter;   
    private Long userId; 
	public String getMerchantOrderId() {
		return merchantOrderId;
	}
	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Integer getExpireAfter() {
		return expireAfter;
	}
	public void setExpireAfter(Integer expireAfter) {
		this.expireAfter = expireAfter;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}