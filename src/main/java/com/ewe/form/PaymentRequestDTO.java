package com.ewe.form;

public class PaymentRequestDTO {
    private Double amtCredit;
    private Double amtDebit;
    private Double currentBalance;
    private String status;
    private String comment;
    private Long userId;
    
    // Razorpay callback fields
    private String razorpay_order_id;
    private String razorpay_payment_id;
    private String razorpay_signature;
    private String error;
    private String error_description;
    
    // Default constructor
    public PaymentRequestDTO() {}
    
    // Getters and Setters for original fields
    public Double getAmtCredit() {
        return amtCredit;
    }
    
    public void setAmtCredit(Double amtCredit) {
        this.amtCredit = amtCredit;
    }
    
    public Double getAmtDebit() {
        return amtDebit;
    }
    
    public void setAmtDebit(Double amtDebit) {
        this.amtDebit = amtDebit;
    }
    
    public Double getCurrentBalance() {
        return currentBalance;
    }
    
    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    // Getters and Setters for Razorpay fields
    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }
    
    public void setRazorpay_order_id(String razorpay_order_id) {
        this.razorpay_order_id = razorpay_order_id;
    }
    
    public String getRazorpay_payment_id() {
        return razorpay_payment_id;
    }
    
    public void setRazorpay_payment_id(String razorpay_payment_id) {
        this.razorpay_payment_id = razorpay_payment_id;
    }
    
    public String getRazorpay_signature() {
        return razorpay_signature;
    }
    
    public void setRazorpay_signature(String razorpay_signature) {
        this.razorpay_signature = razorpay_signature;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getError_description() {
        return error_description;
    }
    
    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
    
    @Override
    public String toString() {
        return "PaymentRequestDTO{" +
                "amtCredit=" + amtCredit +
                ", amtDebit=" + amtDebit +
                ", currentBalance=" + currentBalance +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", userId=" + userId +
                ", razorpay_order_id='" + razorpay_order_id + '\'' +
                ", razorpay_payment_id='" + razorpay_payment_id + '\'' +
                ", razorpay_signature='" + razorpay_signature + '\'' +
                ", error='" + error + '\'' +
                ", error_description='" + error_description + '\'' +
                '}';
    }
}