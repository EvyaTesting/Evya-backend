package com.ewe.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ewe.dao.GeneralDao;
import com.ewe.exception.InsufficientBalanceException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.RFIDRequestDTO;
import com.ewe.pojo.AccountTransactions;
import com.ewe.pojo.Accounts;
import com.ewe.pojo.RFID;
import com.ewe.pojo.RFIDRequests;
import com.ewe.service.RFIDService;

@Service
public class RFIDServiceImp implements RFIDService {

    @Autowired
    private GeneralDao<RFID, String> generalDao;
    @PersistenceContext
    private EntityManager entityManager;
	 private  static final  Logger LOGGER = LoggerFactory.getLogger(TicketServiceImpl.class);
	 
	 private String generateOrderId() {
		    long seconds = System.currentTimeMillis() / 1000;  
		    int random = (int)(Math.random() * 900) + 100;    
		    return "ORD-" + seconds + "-" + random;
		}
	 
	 @Override
	 public List<RFIDRequests> getRequestedRfidList(Long orgId) {
	     return getRequestedRfidList(orgId, null); // Maintain backward compatibility
	 }

	 @Override
	 public List<RFIDRequests> getRequestedRfidList(Long orgId, String search) {
	     try {
	         // Build the base query
	         StringBuilder requestQuery = new StringBuilder(
	             "SELECT * FROM rfid_request WHERE status = 'PENDING'");
	         
	         // Add orgId filter if provided
	         if (orgId != null) {
	             requestQuery.append(" AND orgId = :orgId");
	         }
	         
	         // Add search condition if provided
	         if (search != null && !search.trim().isEmpty()) {
	             requestQuery.append(" AND (LOWER(order_id) LIKE LOWER(:search) OR " +
	                               "LOWER(first_name) LIKE LOWER(:search) OR " +
	                               "LOWER(last_name) LIKE LOWER(:search) OR " +
	                               "LOWER(email) LIKE LOWER(:search) OR " +
	                               "mobile LIKE :search OR " +
	                               "LOWER(address) LIKE LOWER(:search))");
	         }
	         
	         // Create the query
	         Query nativeQuery = entityManager.createNativeQuery(requestQuery.toString(), RFIDRequests.class);
	         
	         // Set parameters
	         if (orgId != null) {
	             nativeQuery.setParameter("orgId", orgId);
	         }
	         
	         if (search != null && !search.trim().isEmpty()) {
	             nativeQuery.setParameter("search", "%" + search + "%");
	         }
	         
	         @SuppressWarnings("unchecked")
	         List<RFIDRequests> requests = nativeQuery.getResultList();
	         
	         return requests;
	     } catch (Exception e) {
	         LOGGER.error("Error fetching RFID requests", e);
	         return Collections.emptyList();
	     }
	 }

    @Override
    public List<RFID> getIssuedRfidList() {
        try {
            List<RFID> issuedCards = generalDao.findAll(new RFID());
            return issuedCards != null ? issuedCards : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    @Override
    public List<Map<String, Object>> getSingleUserRfid(String userId) {
//        String rfidQuery = "SELECT * FROM rfid WHERE userId = :userId";
//        String requestQuery = "SELECT * FROM rfid_request WHERE userId = :userId";
    	
    	String rfidQuery = "SELECT r.*, u.fullname, u.username " +
                "FROM rfid r " +
                "LEFT JOIN users u ON r.userId = u.id " +
                "WHERE r.userId = :userId";

String requestQuery = "SELECT rr.*, u.fullname, u.username " +
                   "FROM rfid_request rr " +
                   "LEFT JOIN users u ON rr.userId = u.id " +
                   "WHERE rr.userId = :userId";

        Query rfidNativeQuery = entityManager.createNativeQuery(rfidQuery)
            .unwrap(org.hibernate.query.NativeQuery.class)
            .setResultTransformer(org.hibernate.transform.AliasToEntityMapResultTransformer.INSTANCE);
        rfidNativeQuery.setParameter("userId", userId);

        Query requestNativeQuery = entityManager.createNativeQuery(requestQuery)
            .unwrap(org.hibernate.query.NativeQuery.class)
            .setResultTransformer(org.hibernate.transform.AliasToEntityMapResultTransformer.INSTANCE);
        requestNativeQuery.setParameter("userId", userId);

        List<Map<String, Object>> rfidResults = rfidNativeQuery.getResultList();
        List<Map<String, Object>> requestResults = requestNativeQuery.getResultList();

        // Combine both result lists
        List<Map<String, Object>> combined = new ArrayList<>();
        combined.addAll(rfidResults);
        combined.addAll(requestResults);

        // If both are empty, throw or return custom error
//        if (combined.isEmpty()) {
//            throw new RuntimeException("No RFID found for this userId: " + userId);
//            // OR return Collections.singletonList(Map.of("message", "No RFID found for this userId: " + userId));
//        }

        return combined;
    }
    
    @Override
    public List<Map<String, Object>> getSingleRfid(String rfid) {
        String query = "SELECT r.rfId AS rfId, r.phone AS phone, r.rfidHex AS rfidHex, " +
                       "r.status AS rfidStatus, r.expiryDate AS expiryDate, " +
                       "u.username AS username, u.email AS email, u.fullname AS fullname, u.mobileNumber AS mobileNumber " +
                       "FROM rfid r " +
                       "LEFT JOIN Users u ON r.userId = u.id " +
                       "WHERE r.rfId = :rfid";

        Query nativeQuery = entityManager.createNativeQuery(query);

        nativeQuery.setParameter("rfid", rfid);

        @SuppressWarnings("unchecked")
        List<Object[]> results = nativeQuery.getResultList();

        List<Map<String, Object>> formattedResults = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("rfId", row[0]);
            map.put("phone", row[1]);
            map.put("rfidHex", row[2]);
            map.put("rfidStatus", row[3]);
            map.put("expiryDate", row[4]);
            map.put("username", row[5]);
            map.put("email", row[6]);
            map.put("fullname", row[7]);
            map.put("mobileNumber", row[8]);
            formattedResults.add(map);
        }
        return formattedResults;
    }
    
    @Override
    public String deleteRfidRequest(int rfid) {
        try {
            long rfidLong = rfid;
            RFIDRequests request = generalDao.findOneById(new RFIDRequests(), rfidLong);

            if (request != null) {
                generalDao.delete(request);
                return "RFID request with ID " + rfid + " deleted successfully";
            } else {
                throw new IllegalArgumentException("RFID request with ID " + rfid + " not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete RFID request: " + e.getMessage(), e);
        }
    }
    	
    	
    	    @Override
    	    public RFIDRequests createRequest(int userId, RFIDRequestDTO requestDTO, double rfidUnitPrice) 
    	        throws InsufficientBalanceException, UserNotFoundException {
    	        
    	        // 1. Calculate total amount
    	        double totalAmount = requestDTO.getRfidCount() * rfidUnitPrice;
    	        
    	        // 2. Get user account
    	        Accounts account = findAccountByUserId(userId);
    	            if(account==null) {
    	            	throw new RuntimeException("account not found with ID: " + userId);
    	            }
    	        
    	        // 3. Check balance
    	        if(account.getAccountBalance() < totalAmount) {
    	            throw new InsufficientBalanceException("Insufficient balance for RFID request");
    	        }
    	        
    	        // 4. Create request
    	        RFIDRequests request = new RFIDRequests();
    	        request.setUserId(userId);
    	        request.setFirstName(requestDTO.getFirstName());
    	        request.setLastName(requestDTO.getLastName());
    	        request.setMobile(requestDTO.getMobile());
    	        request.setEmail(requestDTO.getEmail());
    	        request.setRfidCount(requestDTO.getRfidCount());
    	        request.setOrgId(requestDTO.getOrgId());
    	        request.setStatus("PENDING");
    	        request.setAddress(requestDTO.getAddress());
    	        request.setOrderId(requestDTO.getOrderId());
    	        request.setCreationDate(new Date());
    	        request.setOrderId(generateOrderId());
    	        
    	        RFIDRequests savedRequest = (RFIDRequests) generalDao.savOrupdate(request);
    	        
    	        try {
    	            // 5. Deduct amount
    	            account.setAccountBalance(account.getAccountBalance() - totalAmount);
    	            generalDao.savOrupdate(account);
    	            
    	            // 6. Create transaction
    	            AccountTransactions transaction = new AccountTransactions();
    	            transaction.setAccount(account);
    	            transaction.setAmtDebit(totalAmount);
    	            transaction.setCurrentBalance(account.getAccountBalance());
    	            transaction.setStatus("COMPLETED");
    	            transaction.setComment("RFID Request for " + requestDTO.getRfidCount() + " tags");
    	            transaction.setTransactionId(generateTransactionId());
    	            request.setOrderId(generateOrderId());
    	            
    	            generalDao.savOrupdate(transaction);
    	            
    	            
    	            
    	        } catch (Exception e) {
    	            // Rollback if transaction fails
    	        	generalDao.delete(savedRequest);
    	            throw new RuntimeException("Transaction failed: " + e.getMessage());
    	        }
				return savedRequest;
    	    }
    	    
    	    private String generateTransactionId() {
    	        return "TXN" + System.currentTimeMillis();
    	    }
    	    
    	    @Override
    	    public Accounts findAccountByUserId(int id) {
    	    	try {
    	    		// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
    	    		String query = "Select * from Accounts where user_id = " + id;
    	    		Accounts net = (Accounts) generalDao.findOneSQLQuery(new Accounts(), query);
    	    		System.out.println("netw :" + net);
    	    		return net;
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    	return null;
    	}
    	    @Override
    	    public RFID findByUserId(long id) {
    	    	try {
    	    		// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
    	    		String query = "Select * from rfid where user_id = " + id;
    	    		RFID net = (RFID) generalDao.findOneSQLQuery(new RFID(), query);
    	    		System.out.println("netw :" + net);
    	    		return net;
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    	return null;
    	}
    	    
    	    @Override
    	    public RFID findByRfid(String  id) {
    	    	try {
    	    		// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
    	    		String query = "SELECT * FROM rfid WHERE rfId = '" + id + "'";

    	    		RFID net = (RFID) generalDao.findOneSQLQuery(new RFID(), query);
    	    		System.out.println("netw :" + net);
    	    		return net;
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    	return null;
         }    	    

    	    @Override
    	    public List<RFID> updateRfids(long userId, List<RFID> rfidDetailsList,Long id) throws UserNotFoundException {
    	        List<RFID> savedRfids = new ArrayList<>();
    	        RFIDRequests request = (RFIDRequests) generalDao.findOneById(new RFIDRequests(), id);
    	        if(request == null) {
    	            throw new UserNotFoundException("RFID Request not found with ID: " + id);
    	        }
    	        if(request.getUserId() != userId) {
    	            throw new RuntimeException("RFID Request does not belong to this user");
    	        }
    	        if(!"PENDING".equals(request.getStatus())) {
    	            throw new RuntimeException("RFID Request is not in PENDING status");
    	        }
    	        if(rfidDetailsList.size() != request.getRfidCount()) {
    	            throw new RuntimeException("Number of RFIDs provided doesn't match request count");
    	        }
    	        for (RFID rfidDetails : rfidDetailsList) {
    	            RFID rfid = new RFID();
    	            rfid.setRfId(rfidDetails.getRfId());
    	            rfid.setExpiryDate(rfidDetails.getExpiryDate());
    	           // rfid.setUserId(userId); // Or rfidDetails.getUserId() if frontend provides it
    	            rfid.setPhone(rfidDetails.getPhone());
    	            rfid.setAccount(rfidDetails.getAccount());
    	            rfid.setUserId(rfidDetails.getUserId());
    	            rfid.setStatus("Inactive"); // Default status
    	            savedRfids.add(generalDao.save(rfid));
    	        }				
				generalDao.delete(request);
				    	        return savedRfids;
    	    }

    	    @Override
    		public void toggleRfidStatus(String rfidId) throws Exception {
    		    RFID rfid = findByRfid(rfidId); // Fixed here
    		    if (rfid == null) {
    		        throw new Exception("RFID not found for ID: " + rfidId);
    		    }
    		    String currentStatus = rfid.getStatus();
    		    if ("Active".equalsIgnoreCase(currentStatus)) {
    		        rfid.setStatus("Inactive");
    		    } else {
    		        rfid.setStatus("Active");
    		    }

    		    generalDao.update(rfid);
    		}
			

    	    }