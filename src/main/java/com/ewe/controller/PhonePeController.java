package com.ewe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ewe.pojo.PhonePeOrderStatusResponse;
import com.ewe.pojo.PhonePePaymentRequest;
import com.ewe.pojo.PhonePePaymentResponse;
import com.ewe.pojo.PhonePeTokenResponse;
import com.ewe.service.PhonePeService;

@RestController
@RequestMapping("/api/phonepe")
public class PhonePeController {

    private final PhonePeService phonePeService;

    public PhonePeController(PhonePeService phonePeService) {
        this.phonePeService = phonePeService;
    }

    @GetMapping("/token")
    public ResponseEntity<PhonePeTokenResponse> getToken() {
        return ResponseEntity.ok(phonePeService.generateToken());
    }

    @PostMapping("/pay")
    public ResponseEntity<PhonePePaymentResponse> createPayment(
            @RequestBody PhonePePaymentRequest request) {
        return ResponseEntity.ok(phonePeService.createPayment(request));
    }

    @GetMapping("/status/{merchantOrderId}")
    public ResponseEntity<PhonePeOrderStatusResponse> checkStatus(
            @PathVariable String merchantOrderId) {
        return ResponseEntity.ok(phonePeService.checkOrderStatus(merchantOrderId));
    }

    // ← New — verify payment and update wallet
    @GetMapping("/verify/{merchantOrderId}")
    public ResponseEntity<PhonePeOrderStatusResponse> verifyAndUpdateWallet(
            @PathVariable String merchantOrderId) {
        return ResponseEntity.ok(phonePeService.verifyAndUpdateWallet(merchantOrderId));
    }
}