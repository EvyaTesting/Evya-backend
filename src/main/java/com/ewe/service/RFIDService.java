package com.ewe.service;



import java.util.List;
import java.util.Map;

import com.ewe.exception.InsufficientBalanceException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.RFIDRequestDTO;
import com.ewe.pojo.Accounts;
import com.ewe.pojo.RFID;
import com.ewe.pojo.RFIDRequests;

public interface RFIDService {
    List<RFIDRequests> getRequestedRfidList(Long orgId, String search);
    List<RFID> getIssuedRfidList();
    List<Map<String, Object>> getSingleUserRfid(String userId);
    List<Map<String, Object>> getSingleRfid(String rfid);
    List<RFIDRequests> getRequestedRfidList(Long orgId);
	String deleteRfidRequest(int rfid);
	

		
		
		    RFIDRequests createRequest(int userId, RFIDRequestDTO requestDTO, double rfidUnitPrice) 
		        throws InsufficientBalanceException, UserNotFoundException;

			
			Accounts findAccountByUserId(int id);

			
			List<RFID> updateRfids(long userId, List<RFID> rfidDetailsList, Long id) throws UserNotFoundException;


				RFID findByUserId(long id);
				void toggleRfidStatus(String id)throws Exception;
				RFID findByRfid(String id);
			}
