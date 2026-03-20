package com.ewe.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ewe.cenum.ErrorCodes;
import com.ewe.controller.advice.ServerException;
import com.ewe.exception.InsufficientBalanceException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.ApiResponse;
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
import com.ewe.service.MobileAPIService;
import io.swagger.annotations.ApiOperation;



@RestController
@RequestMapping("/api/mobile")
public class MobileAPIController {

	@Autowired
	private MobileAPIService mobileAPIService;
	 @Value("${rfid.unit.price}")
	    private double rfidUnitPrice;
	
	final static Logger logger = LoggerFactory.getLogger(MobileAPIController.class);

	@ApiOperation(value = "Get OTP")
	@RequestMapping(value = "/otp/{phoneNo}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<Map<String, Object>>> getOTP(@PathVariable String phoneNo)
	        throws UserNotFoundException {

	    logger.info("MobileAPIController.getOTP() - by [" + phoneNo + "]");

	    List<Map<String, Object>> otpData = mobileAPIService.getOTP(phoneNo);
	    Map<String, Object> result = otpData.get(0); // Expecting single map with "status"

	    String status = (String) result.get("status");

	    if ("OTP sent successfully".equalsIgnoreCase(status)) {
	        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
	            true,
	            "otp sent successfully",
	            new HashMap<>());
	        return ResponseEntity.ok(response);

	    } else if (status != null && status.toLowerCase().contains("no mobile number")) {
	        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
	            false,
	            "Invalid mobile number.",
	            1306,
	            new HashMap<>());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

	    } else {
	        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
	            false,
	            "Error occurred while sending OTP",
	            5001,
	            new HashMap<>());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@ApiOperation(value = "Verify OTP")
	@RequestMapping(value = "/otp/verify", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOtp(@RequestParam String otp)
	        throws UserNotFoundException {

	    logger.info("MobileAPIController.verifyOtp() - by [" + otp + "]");

	    List<Map<String, Object>> resultList = mobileAPIService.VerifyOtp(otp);
	    Map<String, Object> result = resultList.get(0);
	    String status = (String) result.get("status");

	    if ("OTP verified successfully".equalsIgnoreCase(status)) {
	        Map<String, Object> data = new HashMap<>();
	        data.put("mobilenumber", result.get("phone"));
	        data.put("otp", otp);
	        data.put("userId", result.get("userId")); // include userId

	        return ResponseEntity.ok(new ApiResponse<>(
	            true,
	            "OTP verified successfully",
	            data
	        ));
	    } else if ("OTP expired".equalsIgnoreCase(status)) {
	        return ResponseEntity.status(HttpStatus.GONE).body(new ApiResponse<>(
	            false,
	            "OTP expired",
	            1308,
	            new HashMap<>()
	        ));
	    } else if ("Invalid or expired OTP".equalsIgnoreCase(status)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
	            false,
	            "Invalid or expired OTP",
	            1307,
	            new HashMap<>()
	        ));
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
	            false,
	            "Error occurred while verifying OTP",
	            5002,
	            new HashMap<>()
	        ));
	    }
	}

	@ApiOperation(value = "Get profile By Id")
	@RequestMapping(value = "/userDetails/{id}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<User>> getProfileById(@PathVariable Long id)
	        throws InterruptedException {
	    try {
	        User user = mobileAPIService.getProfileById(id);

	        return ResponseEntity.ok(new ApiResponse<>(
	            true,
	            "User profile retrieved successfully",
	            user
	        ));

	    } catch (UserNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
	            false,
	            ex.getMessage(),
	            1404,
	            null
	        ));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
	            false,
	            "An unexpected error occurred",
	            1500,
	            null
	        ));
	    }
	}
	
	@ApiOperation(value = "Update User Details")
	@RequestMapping(value = "/updateUserDetails", method = RequestMethod.PUT)
	public ResponseEntity<ApiResponse<User>> updateUser(@RequestBody UserDetailsForm usersForm) {
	    try {
	        User updatedUser = mobileAPIService.updateUserDetails(usersForm);
	        return ResponseEntity.ok(
	            new ApiResponse<User>(true, "User updated successfully", updatedUser)
	        );
	    } catch (ServerException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	            new ApiResponse<User>(false, e.getMessage(), Integer.parseInt(e.getKey()), null)
	        );
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<User>(false, "Internal Server Error", 5000, null)
	        );
	    }
	}

	@ApiOperation(value = "User Registration")
	@RequestMapping(value = "/userRegistartion", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserRegistartion(@RequestBody UserDetailsForm usersForm) {
	    logger.info("MobileAPIController.getUserRegistartion() - by  [" + usersForm + "]");
	    
	    try {
	        List<Map<String, Object>> result = mobileAPIService.registerUser(usersForm);
	        return ResponseEntity.ok(
	            new ApiResponse<>(true, "User registered successfully", result)
	        );
	    } catch (ServerException e) {
	        // Custom known exceptions
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	            new ApiResponse<>(false, e.getMessage(), Integer.parseInt(e.getKey()), null)
	        );

	    } catch (Exception e) {
	        // Fallback for unknown issues
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Internal Server Error", 5000, null)
	        );
	    }
	}
	
	@ApiOperation(value = "adding address to the user")
	@RequestMapping(value = "/addAddress/{userId}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<?> addAddress(@PathVariable("userId") Long userId, @RequestBody Address address) {
        try {
            Address savedAddress = mobileAPIService.addOrUpdateAddress(userId, address);
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("status", "success");
            response.put("address", savedAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<String, Object>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
	
	@ApiOperation(value = "delete the address")
	@RequestMapping(value= "/deleteAddress/{addressId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteAddress(@PathVariable("addressId") Long addressId) {
		try {
			mobileAPIService.deleteAddressById(addressId);
			Map<String , Object> response = new HashMap<>();
	        response.put("message", "Address deleted successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map <String, Object> error = new HashMap<String, Object>();
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}

	@ApiOperation(value = "Get EV Brand")
	@RequestMapping(value = "/evBrand", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<EV_Brands>>> getBrand() throws UserNotFoundException, ServerException {
	    try {
	        List<EV_Brands> brands = mobileAPIService.getBrand();
	        return ResponseEntity.ok(
	            new ApiResponse<>(true, "EV Brands fetched successfully", brands)
	        );
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Internal Server Error", 5000, null)
	        );
	    }
	}

	@ApiOperation(value = "Add the EV vehicle")
	@RequestMapping(value = "/addEV", method = RequestMethod.POST)
	public ResponseEntity<ApiResponse<Object>> addEV(@RequestBody VehicleForm vehicleForm) {
	    try {
	        List<Map<String, Object>> result = mobileAPIService.addEV(vehicleForm);
	        
	        // Check if the operation was successful
	        if (result != null && !result.isEmpty()) {
	            Map<String, Object> firstResult = result.get(0);
	            String status = (String) firstResult.get("status");		      
	            boolean isSuccess = "EV Added Successfully".equals(status);		            
	            return ResponseEntity.ok(
	                new ApiResponse<>(isSuccess, isSuccess ? "EV Added Successfully" : status, result)
	            );
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	                new ApiResponse<>(false, "No result returned from service", 500, null)
	            );
	        }
	        
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	            new ApiResponse<>(false, e.getMessage(), 404, null)
	        );
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Internal Server Error: " + e.getMessage(), 500, null)
	        );
	    }
	}
	
	@ApiOperation(value = "Get the EV vehicle based on user ID")
	@RequestMapping(value = "/myEV/{userId}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<Vehicles>>> getMyEV(@PathVariable long userId) {
	    try {
	        List<Vehicles> vehicles = mobileAPIService.getMyEVG(userId);

	        if (vehicles.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                new ApiResponse<>(false, "No vehicles found for user ID: " + userId, 404, null)
	            );
	        }

	        return ResponseEntity.ok(
	            new ApiResponse<>(true, "Vehicles retrieved successfully", vehicles)
	        );

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Internal Server Error", 5000, null)
	        );
	    }
	}

	@ApiOperation(value = "Get the wallet details based on user ID")
	@RequestMapping(value = "/walletDetails/{userId}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<Accounts>>> getWalletDetails(@PathVariable long userId) throws UserNotFoundException {
	    try {
	        List<Accounts> walletDetails = mobileAPIService.getWalletDetails(userId);

	        if (walletDetails == null || walletDetails.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                new ApiResponse<>(false, "No wallet details found for user ID: " + userId, 404, null)
	            );
	        }

	        return ResponseEntity.ok(
	            new ApiResponse<>(true, "Wallet details retrieved successfully", walletDetails)
	        );

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Internal Server Error", 5000, null)
	        );
	    }
	}
	
	@ApiOperation(value = "Get the wallet transaction details based on account ID")
	@RequestMapping(value = "/walletTransactions/{accId}", method = RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<AccountTransactions>>> getWalletTransactions(@PathVariable long accId) throws UserNotFoundException {
	    try {
	        List<AccountTransactions> transactions = mobileAPIService.getWalletTransactions(accId);

	        if (transactions == null || transactions.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                new ApiResponse<>(false, "No wallet transactions found for account ID: " + accId, 404, null)
	            );
	        }

	        return ResponseEntity.ok(
	            new ApiResponse<>(true, "Wallet transactions retrieved successfully", transactions)
	        );

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Internal Server Error", 5000, null)
	        );
	    }
	}
	
	@ApiOperation(value = "Update Vehicle")
	@RequestMapping(value = "/updateEV/{id}", method = RequestMethod.PUT)
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateVehicle(
	        @PathVariable("id") Long vehicleId,
	        @RequestBody Vehicles vehicleForm
	) {
	    logger.info("MobileAPIController.updateVehicle() - by Vehicle ID [{}]", vehicleId);
	
	    try {
	        ResponseMessage response = mobileAPIService.updateVehicle(vehicleId, vehicleForm);
	
	        Map<String, Object> data = new HashMap<>();
	        data.put("status", response.getMessage());
	
	        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle updated successfully", data));
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                new ApiResponse<>(false, e.getMessage(), 404, null)
	        );
	    } catch (ServerException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	                new ApiResponse<>(false, e.getMessage(), Integer.parseInt(e.getKey()), null)
	        );
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	                new ApiResponse<>(false, "Internal Server Error", 5000, null)
	        );
	    }
	}

	@ApiOperation(value = "Get user RFID details")
    @GetMapping("/rfid/user/{userId}")
    public ResponseEntity<ApiResponse<Map<String, List<Map<String, Object>>>>> getSingleUserRfid(@PathVariable String userId) {
        try {
            List<Map<String, Object>> rfids = mobileAPIService.getSingleUserRfid(userId);
            if (rfids == null || rfids.isEmpty()) {
                return createErrorResponse("No RFID found for this user", ErrorCodes.RFID_NOT_FOUND, HttpStatus.OK);
            }
            return createSuccessResponse("RFIDs retrieved successfully", Map.of("rfids", rfids));
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve RFIDs: " + e.getMessage(), 
                                    ErrorCodes.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@ApiOperation(value = "Delete RFID request")
    @DeleteMapping("/rfid/request/{rfid}")
    public ResponseEntity<ApiResponse<Void>> deleteRfidRequest(@PathVariable int rfid) {
        try {
            String message = mobileAPIService.deleteRfidRequest(rfid);
            return createSuccessResponse(message, null);
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage(), ErrorCodes.RFID_REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("Failed to delete RFID request: " + e.getMessage(), 
                                    ErrorCodes.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Create RFID request")
    @PostMapping("/rfid/request/{userId}")
    public ResponseEntity<ApiResponse<RFIDRequests>> createRfidRequest(
            @PathVariable int userId,
            @RequestBody RFIDRequestDTO requestDTO) {
        try {
            // Validate required fields
            if (requestDTO.getFirstName() == null || requestDTO.getFirstName().isEmpty()) {
                return createErrorResponse("First name is required", ErrorCodes.REG_FIRSTNAME_INVALID, HttpStatus.BAD_REQUEST);
            }

            if (requestDTO.getMobile() == null || requestDTO.getMobile().isEmpty()) {
                return createErrorResponse("Mobile number is required", ErrorCodes.REG_MOBILE_NULL, HttpStatus.BAD_REQUEST);
            }

            RFIDRequests request = mobileAPIService.createRequest(userId, requestDTO, rfidUnitPrice);
            return createSuccessResponse("RFID request created successfully", request);
        } catch (InsufficientBalanceException e) {
            return createErrorResponse(e.getMessage(), ErrorCodes.INSUFFICIENT_BALANCE, HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return createErrorResponse(e.getMessage(), ErrorCodes.INVALID_USER, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("Failed to create RFID request: " + e.getMessage(), 
                                    ErrorCodes.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Toggle RFID status")
    @PostMapping("/rfid/toggle-status/{rfidId}")
    public ResponseEntity<ApiResponse<Void>> toggleRfidStatus(@PathVariable String rfidId) {
        try {
            mobileAPIService.toggleRfidStatus(rfidId);
            return createSuccessResponse("RFID status toggled successfully", null);
        } catch (Exception e) {
            ErrorCodes errorCode = ErrorCodes.INTERNAL_SERVER_ERROR;
            String message = e.getMessage();
            
            if (message.contains("not found")) {
                errorCode = ErrorCodes.RFID_NOT_FOUND;
            } else if (message.contains("already active")) {
                errorCode = ErrorCodes.RFID_ALREADY_ACTIVE;
            } else if (message.contains("already inactive")) {
                errorCode = ErrorCodes.RFID_ALREADY_INACTIVE;
            }

            return createErrorResponse(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createErrorResponse(String message, ErrorCodes errorCode, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setError_code(errorCode.getCode());
        return ResponseEntity.status(status).body(response);
    }
    
    @ApiOperation(value = "Create Payment Transaction")
    @PostMapping(value = "/add_payment")
    public ResponseEntity<ApiResponse<AccountTransactions>> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            System.out.println("Received DTO: " + paymentRequestDTO);
            AccountTransactions payment = mobileAPIService.createPayment(paymentRequestDTO);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment transaction created successfully", payment)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Failed to create payment transaction: " + e.getMessage(), 5000, null)
            );
        }}

    @PostMapping("/handle-payment-callback")
    public ResponseEntity<ApiResponse<String>> handlePaymentCallback(@RequestBody PaymentRequestDTO callbackDTO) {
        try {
            System.out.println("=== Received Payment Callback ===");
            System.out.println("Callback DTO: " + callbackDTO);
            
            if (callbackDTO.getRazorpay_order_id() == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Order ID is required", 4000, null)
                );
            }
            
            String result = mobileAPIService.updatePaymentStatus(callbackDTO);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment callback handled successfully", result)
            );
        } catch (RuntimeException e) {
            System.err.println("Callback processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Failed to handle payment callback: " + e.getMessage(), 5000, null)
            );
        } catch (Exception e) {
            System.err.println("Unexpected error in callback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Unexpected error: " + e.getMessage(), 5001, null)
            );
        }
    }

	@ApiOperation(value = "Capture Payment")
	@PostMapping("/capture-payment")
	public ResponseEntity<ApiResponse<String>> capturePayment(
	        @RequestParam(name = "paymentId") String paymentId,
	        @RequestParam(name = "amount") int amount) {
	    try {
	        String message = mobileAPIService.capturePayment(paymentId, amount);
	        return ResponseEntity.ok(
	            new ApiResponse<>(true, "Payment captured successfully", message)
	        );
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Failed to capture payment: " + e.getMessage(), 5000, null)
	        );
	    }
	}

	@ApiOperation(value = "Refund Payment")
	@PostMapping("/refund-payment")
	public ResponseEntity<ApiResponse<String>> refundPayment(
	        @RequestParam(name = "paymentId") String paymentId,
	        @RequestParam(name = "amount") int amount) {
	    try {
	        String message = mobileAPIService.refundPayment(paymentId, amount);
	        return ResponseEntity.ok(
	            new ApiResponse<>(true, "Payment refunded successfully", message)
	        );
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Failed to refund payment: " + e.getMessage(), 5000, null)
	        );
	    }
	}

	@ApiOperation(value = "Get Payment Transaction by Order ID")
	@GetMapping("/get-payment")
	public ResponseEntity<ApiResponse<AccountTransactions>> getPaymentByOrderId(@RequestParam("orderId") String orderId) {
	    try {
	        AccountTransactions payment = mobileAPIService.getPaymentByOrderId(orderId);
	        if (payment != null) {
	            return ResponseEntity.ok(
	                new ApiResponse<>(true, "Payment transaction retrieved successfully", payment)
	            );
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                new ApiResponse<>(false, "Payment transaction not found for order ID: " + orderId, 404, null)
	            );
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	            new ApiResponse<>(false, "Failed to retrieve payment transaction: " + e.getMessage(), 5000, null)
	        );
	    }
	}

	@PostMapping("/calculate")
    public Map<String, Object> calculateEnergy(
            @RequestParam double portCapacity,
            @RequestParam double pricePerUnit,
            @RequestBody Map<String, Object> body) {

        return mobileAPIService.calculate(portCapacity, pricePerUnit, body);
    }
	
	@GetMapping("/mobilegetAllSites")
	public ResponseEntity<List<GetSiteDetailsDto>> getAllSites(
	        @RequestParam(required = false) String powerType,
	        @RequestParam(required = false) String connectorTypes,
	        @RequestParam(required = false) String status,
	        @RequestParam(required = false) String powerRange,
	        @RequestParam(required 	= false) String priceRange,
	        @RequestParam(required = false) Double latitude,
	        @RequestParam(required = false) Double longitude,
	        @RequestParam(required = false) String search
	) {

	    return ResponseEntity.ok(
	            mobileAPIService.getAllSiteDetailsMobileapp(
	                    powerType,
	                    connectorTypes,
	                    status,
	                    powerRange,
	                    priceRange,
	                    latitude,
	                    longitude,
	                    search));}
	
	@GetMapping("/Details")
    public ResponseEntity<List<SiteDetailsResponseDto>> getSiteDetails(
            @RequestParam Long siteId) {

        List<SiteDetailsResponseDto> response =
        		mobileAPIService.getSiteDetailsBySiteId(siteId);

        return ResponseEntity.ok(response);
    }
}
