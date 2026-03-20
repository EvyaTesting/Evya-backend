package com.ewe.service;

import com.ewe.pojo.PhonePeOrderStatusResponse;
import com.ewe.pojo.PhonePePaymentRequest;
import com.ewe.pojo.PhonePePaymentResponse;
import com.ewe.pojo.PhonePeTokenResponse;

public interface PhonePeService {
    PhonePeTokenResponse generateToken();
    PhonePePaymentResponse createPayment(PhonePePaymentRequest request);
    PhonePeOrderStatusResponse checkOrderStatus(String merchantOrderId);  // Add this
    PhonePeOrderStatusResponse verifyAndUpdateWallet(String merchantOrderId); // ← Add


}