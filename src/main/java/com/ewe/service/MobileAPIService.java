package com.ewe.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ewe.controller.advice.ServerException;
import com.ewe.exception.InsufficientBalanceException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.GetSiteDetailsDto;
import com.ewe.form.PaymentRequestDTO;
import com.ewe.form.RFIDRequestDTO;
import com.ewe.form.SiteDetailsResponseDto;
import com.ewe.form.UserDetailsForm;
import com.ewe.form.VehicleForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.AccountTransactions;
import com.ewe.pojo.Accounts;
import com.ewe.pojo.Address;
import com.ewe.pojo.EV_Brands;
import com.ewe.pojo.RFIDRequests;
import com.ewe.pojo.User;
import com.ewe.pojo.Vehicles;

public interface MobileAPIService {

	List<Map<String, Object>> getOTP(String phoneNo);
	
	
  List<Map<String, Object>> registerUser(UserDetailsForm usersForm) throws UserNotFoundException, ServerException;

	User getProfileById(Long id) throws UserNotFoundException;

	List<EV_Brands> getBrand();

	List<Map<String, Object>> addEV(VehicleForm vehicleForm) throws UserNotFoundException;

	List<Vehicles> getMyEVG(long userId);

	User updateUserDetails(UserDetailsForm usersForm) throws UserNotFoundException, ServerException;

	List<Accounts> getWalletDetails(long userId);

	List<AccountTransactions>  getWalletTransactions(long accId);

	List<Map<String, Object>> VerifyOtp(String otp);

	//ResponseMessage updateVehicle(Vehicles vehicleForm) throws UserNotFoundException, ServerException;

	void deleteVehicleById(Long id) throws UserNotFoundException;

	User getVehicleById(Long id);


	ResponseMessage updateVehicle(Long vehicleId, Vehicles vehicleForm) throws UserNotFoundException, ServerException;


	RFIDRequests createRequest(int userId, RFIDRequestDTO requestDTO, double rfidUnitPrice)
			throws InsufficientBalanceException, UserNotFoundException;

	String deleteRfidRequest(int rfid);

	void toggleRfidStatus(String rfidId) throws Exception;

	List<Map<String, Object>> getSingleUserRfid(String userId);

	Address addOrUpdateAddress(Long userId, Address address) throws Exception;
	
	String deleteAddressById(Long addId) throws Exception;

	String updatePaymentStatus(PaymentRequestDTO callbackDTO) throws Exception;

	String capturePayment(String paymentId, int amount) throws Exception;

	String refundPayment(String paymentId, int amount) throws Exception;

	AccountTransactions getPaymentByOrderId(String orderId) throws Exception;

	AccountTransactions createPayment(PaymentRequestDTO paymentRequestDTO) throws Exception;
	//void deleteUserById(Long userId) throws UserNotFoundException;


	Map<String, Object> calculate(double portCapacity, double pricePerUnit, Map<String, Object> body);


	List<GetSiteDetailsDto> getAllSiteDetailsMobileapp(String powerType, String connectorTypes, String status, String powerRange,
			String priceRange, Double latitude, Double longitude, String search);


	List<SiteDetailsResponseDto> getSiteDetailsBySiteId(Long siteId);

	}