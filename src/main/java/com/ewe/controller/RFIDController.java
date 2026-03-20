package com.ewe.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ewe.exception.InsufficientBalanceException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.RFIDRequestDTO;
import com.ewe.pojo.RFID;
import com.ewe.pojo.RFIDRequests;
import com.ewe.service.RFIDService;

@RestController
@RequestMapping("/services/rfid")
public class RFIDController {
    
    @Autowired
    private RFIDService rfidservice;
    
    @GetMapping("/RequestedRfidList")
    public ResponseEntity<Map<String, Object>> getRequestedRfidList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String search) {
        
        List<RFIDRequests> allRfids = rfidservice.getRequestedRfidList(orgId, search);
        
        int start = page * size;
        int end = Math.min(start + size, allRfids.size());
        
        if (start >= allRfids.size() && allRfids.size() > 0) {
            start = 0;
            end = Math.min(size, allRfids.size());
        }
        
        List<RFIDRequests> pagedRfids = start < end ? allRfids.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> response = new HashMap<>();
        response.put("rfids", pagedRfids);
        response.put("currentPage", page);
        response.put("totalItems", allRfids.size());
        response.put("totalPages", (int) Math.ceil((double) allRfids.size() / size));
        
        if (pagedRfids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/IssuedRfidList")
    public ResponseEntity<Map<String, Object>> getIssuedRfidList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<RFID> allRfids = rfidservice.getIssuedRfidList();
        int start = page * size;
        int end = Math.min(start + size, allRfids.size());
        if (start >= allRfids.size() && allRfids.size() > 0) {
            start = 0;
            end = Math.min(size, allRfids.size());
        }
        List<RFID> pagedRfids = start < end ? allRfids.subList(start, end) : Collections.emptyList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("rfids", pagedRfids);
        response.put("currentPage", page);
        response.put("totalItems", allRfids.size());
        response.put("totalPages", (int) Math.ceil((double) allRfids.size() / size));
        
        if (pagedRfids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).body(response);
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/SingleUserRfid/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getSingleUserRfid(@PathVariable String userId) {
    	List<Map<String, Object>> rfids = rfidservice.getSingleUserRfid(userId);
        if (rfids == null || rfids.isEmpty()) {
            return null;
        }
        return ResponseEntity.ok(rfids);
    }
    
    @GetMapping("/singleRfid/{rfid}")
    public ResponseEntity<?> getSingleRfid(@PathVariable String rfid) {
    	List<Map<String, Object>> rfidDetails = rfidservice.getSingleRfid(rfid);
        if (rfidDetails == null || rfidDetails.isEmpty()) {
            return ResponseEntity.ok("No Rfid Found");
        }
        return ResponseEntity.ok(rfidDetails);
    }
    
    @DeleteMapping("/deleteRfidRequest/{rfid}")
    public ResponseEntity<?> deleteRfidRequest(@PathVariable int rfid) {
        try {
            rfidservice.deleteRfidRequest(rfid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                               .body("Failed to delete RFID request: " + e.getMessage());
        }
    }
   
      @Value("${rfid.unit.price}")
    	    private double rfidUnitPrice;
    	    @PostMapping("/request/{userId}")
    	    public ResponseEntity<?> createRfidRequest(
    	            @PathVariable int userId,
    	            @RequestBody RFIDRequestDTO requestDTO) throws UserNotFoundException {
    	        
    	        try {
    	            RFIDRequests request = rfidservice.createRequest(userId, requestDTO, rfidUnitPrice);
    	            return ResponseEntity.ok(request);
    	        } catch (InsufficientBalanceException e) {
    	            return ResponseEntity.badRequest().body(e.getMessage());
    	        }
    	    }
    	    
    	    @PostMapping("/update/{userId}")
    	    public ResponseEntity<?> updateRfids(@RequestParam Long id,@PathVariable long userId, @RequestBody List<RFID> rfidDetailsList) {
    	        try {
    	            List<RFID> updatedList = rfidservice.updateRfids(userId, rfidDetailsList,id);
    	            return ResponseEntity.ok(updatedList);
    	        } catch (UserNotFoundException e) {
    	            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(e.getMessage());
    	        } catch (Exception e) {
    	            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Update failed: " + e.getMessage());
    	        }
    	    }

    	    @PostMapping("/toggle-status/{id}")
    	    public ResponseEntity<String> toggleRfidStatus(@PathVariable("id") String id) {
    	        try {
    	            rfidservice.toggleRfidStatus(id);
    	            return ResponseEntity.ok("RFID status toggled successfully.");
    	        } catch (Exception e) {
    	            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Error: " + e.getMessage());
    	        }
    	    }
    	}