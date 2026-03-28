package com.ewe.serviceImpl;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.ewe.cenum.ErrorCodes;
import com.ewe.config.TwilioConfig;
import com.ewe.controller.advice.ServerException;
import com.ewe.dao.GeneralDao;
import com.ewe.exception.InsufficientBalanceException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.GetSiteDetailsDto;
import com.ewe.form.LocationDto;
import com.ewe.form.PaymentRequestDTO;
import com.ewe.form.PortDto;
import com.ewe.form.PortsDto;
import com.ewe.form.RFIDRequestDTO;
import com.ewe.form.SiteDetailsResponseDto;
import com.ewe.form.StationsDto;
import com.ewe.form.StatusNotificationDtos;
import com.ewe.form.UserDetailsForm;
import com.ewe.form.VehicleForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.AccountTransactions;
import com.ewe.pojo.Accounts;
import com.ewe.pojo.Address;
import com.ewe.pojo.DeviceDetails;
import com.ewe.pojo.EV_Brands;
import com.ewe.pojo.OtpRecord;
import com.ewe.pojo.Password;
import com.ewe.pojo.Port;
import com.ewe.pojo.RFID;
import com.ewe.pojo.RFIDRequests;
import com.ewe.pojo.Site;
import com.ewe.pojo.Station;
import com.ewe.pojo.StatusNotification;
import com.ewe.pojo.User;
import com.ewe.pojo.Usersinroles;
import com.ewe.pojo.Vehicles;
import com.ewe.service.EmailService;
import com.ewe.service.MobileAPIService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
@Transactional
public  class MobileAPIServiceImpl  implements MobileAPIService	{

	@Autowired
	private GeneralDao<?, ?> generalDao;
	 @Autowired
	    private TwilioConfig twilioConfig;
	 @Autowired
		private EmailService emailservice;
		private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	 @PersistenceContext
	    private EntityManager entityManager;
	 
	 @Value("${razorpay.key.id}")
	 private String razorPayKey;

	 @Value("${razorpay.secret.key}")
	 private String razorpaySecret;

	 private RazorpayClient razorpayClient;
	 

	 private static final int OTP_EXPIRY_MINUTES = 5;
	 private Map<String, OtpRecord> otpStore = new ConcurrentHashMap<>();

	 
	 @Override
public List<Map<String, Object>> getOTP(String phoneNo) {
    List<Map<String,Object>> list = new ArrayList<>();
    Map<String,Object> result = new HashMap<>();

    try {
        String query = "SELECT id FROM Users WHERE mobilenumber = '" + phoneNo + "'";
        List<Map<String, Object>> data = generalDao.getMapData(query);

        if (data == null || data.isEmpty()) {
            result.put("status", "No mobile number found");
        } else {
            String otp = String.valueOf((int)(Math.random() * 9000) + 1000);

            Message.creator(
                new PhoneNumber("+91" + phoneNo),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                "Your OTP is: " + otp
            ).create();

            Long userId = Long.valueOf(data.get(0).get("id").toString());

            // ✅ Save OTP + phone + timestamp + userId
            otpStore.put(phoneNo, new OtpRecord(otp, phoneNo, LocalDateTime.now(), userId));

            result.put("status", "OTP sent successfully");
        }
    } catch (Exception e) {
        e.printStackTrace();
        result.put("status", "Error occurred while sending OTP");
    }

    list.add(result);
    return list;
}

	 
	 @Override
		
public List<Map<String, Object>> VerifyOtp(String otp) {
    List<Map<String, Object>> list = new ArrayList<>();
    Map<String, Object> result = new HashMap<>();

    try {
        boolean matched = false;

        for (Map.Entry<String, OtpRecord> entry : otpStore.entrySet()) {
            OtpRecord record = entry.getValue();
            if (record.getOtp().equals(otp)) {
                matched = true;

                LocalDateTime now = LocalDateTime.now();
                if (Duration.between(record.getTimestamp(), now).toMinutes() > OTP_EXPIRY_MINUTES) {
                    otpStore.remove(entry.getKey());
                    result.put("status", "OTP expired");
                } else {
                    result.put("status", "OTP verified successfully");
                    result.put("phone", record.getPhoneNumber());
                    result.put("userId", record.getUserId()); // add userId
                    otpStore.remove(entry.getKey()); // Invalidate OTP
                }
                break;
            }
        }

        if (!matched) {
            result.put("status", "Invalid or expired OTP");
        }

    } catch (Exception e) {
        e.printStackTrace();
        result.put("status", "Error occurred while verifying OTP");
    }

    list.add(result);
    return list;
}
	
	 @Transactional
	 @Override
	 public List<Map<String, Object>> registerUser(UserDetailsForm userForm) throws ServerException {
	     try {
	         validateUserDetails(userForm);

	         // Username exists
	         String usernameCheckSql = "SELECT * FROM users WHERE username = '" + userForm.getUsername() + "'";
	         User existingUserByUsername = generalDao.findOneSQLQuery(new User(), usernameCheckSql);
	         if (existingUserByUsername != null) {
	             throw new ServerException("Username already exists: " + userForm.getUsername(), "1001");
	         }

	         // Email exists
	         String emailCheckSql = "SELECT * FROM users WHERE email = '" + userForm.getEmail() + "'";
	         User existingUserByEmail = generalDao.findOneSQLQuery(new User(), emailCheckSql);
	         if (existingUserByEmail != null) {
	             throw new ServerException("Email already exists: " + userForm.getEmail(), "1002");
	         }

	         // Mobile number exists
	         String mobileCheckSql = "SELECT * FROM users WHERE mobileNumber = '" + userForm.getMobileNumber() + "'";
	         User existingUserByMobile = generalDao.findOneSQLQuery(new User(), mobileCheckSql);
	         if (existingUserByMobile != null) {
	             throw new ServerException("Mobile number already exists: " + userForm.getMobileNumber(), "1003");
	         }

	         // Save user
	         User user = new User();
	         user.setUsername(userForm.getUsername());
	         user.setEmail(userForm.getEmail());
	         user.setMobileNumber(userForm.getMobileNumber());
	         user.setEnabled(true);
	         generalDao.save(user);

	         // Address
	         Address address = new Address();
	         address.setAddress(null);
	         address.setCity(null);
	         address.setCountry(null);
	         address.setPhone(null);
	         address.setState(null);
	         address.setZipCode(null);
	         address.setUser(user);
	         generalDao.save(address);

	         // Password
	         Password password = new Password();
	         password.setPassword(null);
	         password.setUser(user);
	         generalDao.save(password);

	         // Account
	         Accounts account = new Accounts();
	         account.setAccountBalance(0);
	         account.setUser(user);
	         generalDao.save(account);
	         
	         RFID rfid=new  RFID();
	         rfid.setUserId(user.getId());
	         rfid.setPhone(user.getMobileNumber());
	         generalDao.save(rfid);

	         // Role and device
	         updatingRoleId("Driver", user.getId(), user);
	         updateDeviceDetails(userForm.getDeviceDetails(), user.getId());

	         // ✅ Send email after successful registration
	         try {
	             emailservice.sendEmailWithTemplate(user.getEmail(), user.getUsername());
	         } catch (Exception emailEx) {
	             logger.error("Failed to send welcome email to " + user.getEmail(), emailEx);
	         }

	         // ✅ Response
	         Map<String, Object> value = new HashMap<>();
	         value.put("status", "User Added Successfully");
	         return List.of(value);

	     } catch (ServerException e) {
	         throw e;
	     } catch (Exception e) {
	         throw new ServerException("Internal Issue While Adding: " + e.getMessage(), "5000");
	     }
	 }




	private void validateUserDetails(UserDetailsForm userForm) throws ServerException {


		if (userForm.getUsername() == null || userForm.getUsername().isEmpty()) {
			throw new ServerException(ErrorCodes.REG_USERNAME_NULL.toString(),
					Integer.toString(ErrorCodes.REG_USERNAME_NULL.getCode()));
		}

		if (userForm.getEmail() == null || !userForm.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
			throw new ServerException(ErrorCodes.REG_EMAIL_INVALID.toString(),
					Integer.toString(ErrorCodes.REG_EMAIL_INVALID.getCode()));
		}

		if (userForm.getMobileNumber() == null || !userForm.getMobileNumber().matches("^\\d{10}$")) {
			throw new ServerException(ErrorCodes.REG_MOBILE_INVALID.toString(),
					Integer.toString(ErrorCodes.REG_MOBILE_INVALID.getCode()));
		}


		 userForm.setPassword(null);
		    userForm.setConfirmPassword(null);
	}
	
	


	private void updateDeviceDetails(Map<String, Object> deviceDetails, Long id) throws UserNotFoundException {
		DeviceDetails details =  new DeviceDetails();
		details.setAppVersion((String) deviceDetails.get("appVersion"));
		details.setDeviceName((String) deviceDetails.get("deviceName"));
		details.setDeviceToken((String) deviceDetails.get("deviceToken"));
		details.setDeviceType((String) deviceDetails.get("deviceType"));
		details.setDeviceVersion((String) deviceDetails.get("deviceVersion"));
		details.setUserId(id);
		generalDao.save(details);		
	}

	private void updatingRoleId(String role, Long id,User users) throws UserNotFoundException {
		String query = "SELECT id FROM role WHERE roleName ='"+role+"'";
		long value =  generalDao.findIdBySqlQuery(query);
		
		Usersinroles user = new Usersinroles();	
		user.setRole_id(value);
		user.setUser(users);;
		generalDao.save(user);
		
	}

	@Override
	public User getProfileById(Long id) throws UserNotFoundException {
	    try {
	        String query = "SELECT * FROM users WHERE id = " + id;
	        User user = generalDao.findOneSQLQuery(new User(), query);

	        if (user == null) {
	            throw new UserNotFoundException("User not found with ID: " + id);
	        }

	        return user;
	    } catch (UserNotFoundException e) {
	        throw e;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error fetching user profile with ID: " + id, e);
	    }
	}




	@Override
	@Transactional
	public List<EV_Brands> getBrand() {
		try {
			return generalDao.findAllSQLQuery(new EV_Brands(), "Select * from ev_brands");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

//	@Override
//	public List<Map<String, Object>> addEV(VehicleForm vehicleForm) throws UserNotFoundException {
//	    List<Map<String, Object>> result = new ArrayList<>();
//	    Map<String, Object> value = new HashMap<>();
//
//	    // Find user
//	    String query = "select * from Users where id=" + vehicleForm.getUserId();
//	    User user = generalDao.findOneSQLQuery(new User(), query);
//
//	    if (user != null) {
//	        try {
//	            Vehicles vehicle = new Vehicles();
//	            vehicle.setConnectorType(vehicleForm.getConnectorType());
//	            vehicle.setVehicleType(vehicleForm.getVehicleType());
//	            vehicle.setDescription(vehicleForm.getDescription());
//	            vehicle.setRegistrationNo(vehicleForm.getRegistrationNo());
//	            vehicle.setUser(user);
//	            
//	            String vin = vehicleForm.getVin();
//	            
//	            // If VIN is provided, decode it using NHTSA API
//	            if (vin != null && !vin.trim().isEmpty()) {
//	                // --- Call NHTSA VIN API ---
//	                RestTemplate restTemplate = new RestTemplate();
//	                String url = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinvaluesextended/" + vin + "?format=json";
//
//	                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
//
//	                // --- Extract first result ---
//	                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("Results");
//	                if (results != null && !results.isEmpty()) {
//	                    Map<String, Object> vinData = results.get(0);
//
//	                    // --- Extract required fields ---
//	                    String make = vinData.getOrDefault("Make", "").toString();
//	                    String makeId = vinData.getOrDefault("MakeID", "").toString();
//	                    String manufacturer = vinData.getOrDefault("Manufacturer", "").toString();
//	                    String manufacturerId = vinData.getOrDefault("ManufacturerId", "").toString();
//	                    String model = vinData.getOrDefault("Model", "").toString();
//	                    String modelId = vinData.getOrDefault("ModelID", "").toString();
//	                    String modelYear = vinData.getOrDefault("ModelYear", "").toString();
//	                    String vehicleVin = vinData.getOrDefault("VIN", "").toString();
//	                    String vehicleDescriptor = vinData.getOrDefault("VehicleDescriptor", "").toString();
//	                    String vehicleType = vinData.getOrDefault("VehicleType", "").toString();
//
//	                    // --- Set vehicle fields from VIN decode ---
//	                    vehicle.setMake(make);
//	                    vehicle.setModel(model);
//	                    vehicle.setYear(modelYear);
//	                    vehicle.setVin(vehicleVin);
//
//	                    // --- Return extracted fields ---
//	                    value.put("status", "EV Added Successfully");
//	                    value.put("Make", make);
//	                    value.put("MakeID", makeId);
//	                    value.put("Manufacturer", manufacturer);
//	                    value.put("ManufacturerId", manufacturerId);
//	                    value.put("Model", model);
//	                    value.put("ModelID", modelId);
//	                    value.put("ModelYear", modelYear);
//	                    value.put("VIN", vehicleVin);
//	                    value.put("VehicleDescriptor", vehicleDescriptor);
//	                    value.put("VehicleType", vehicleType);
//
//	                } else {
//	                    value.put("status", "VIN API returned empty result");
//	                    throw new Exception("VIN API returned empty result");
//	                }
//	            } else {
//	                // If VIN is NOT provided, use manually entered values
//	                vehicle.setMake(vehicleForm.getMake());
//	                vehicle.setModel(vehicleForm.getModel());
//	                vehicle.setYear(vehicleForm.getYear());
//	                vehicle.setVin(""); // Set empty VIN
//	                
//	                // Validate that required fields are provided
//	                if (vehicleForm.getMake() == null || vehicleForm.getMake().trim().isEmpty() ||
//	                    vehicleForm.getModel() == null || vehicleForm.getModel().trim().isEmpty() ||
//	                    vehicleForm.getYear() == null) {
//	                    value.put("status", "Make, Model, and Year are required when VIN is not provided");
//	                    throw new Exception("Make, Model, and Year are required when VIN is not provided");
//	                }
//	                
//	                value.put("status", "EV Added Successfully");
//	                value.put("Make", vehicleForm.getMake());
//	                value.put("Model", vehicleForm.getModel());
//	                value.put("ModelYear", vehicleForm.getYear());
//	                value.put("VIN", "");
//	            }
//
//	            generalDao.save(vehicle);
//
//	        } catch (Exception e) {
//	            value.put("status", "Failed to add vehicle: " + e.getMessage());
//	        }
//
//	    } else {
//	        value.put("status", "User Id Invalid");
//	    }
//
//	    result.add(value);
//	    return result;
//	}
	



@Override
	public List<Map<String, Object>> addEV(VehicleForm vehicleForm) throws UserNotFoundException {

	    List<Map<String, Object>> result = new ArrayList<>();
	    Map<String, Object> value = new HashMap<>();

	    String query = "select * from Users where id=" + vehicleForm.getUserId();
	    User user = generalDao.findOneSQLQuery(new User(), query);

	    if (user != null) {
	        try {
	            // Fetch the variant by ID first
	            EV_Variants variant = generalDao.findOneById(new EV_Variants(), vehicleForm.getVariantId());
	            
	            if (variant == null) {
	                value.put("status", "Error: Variant not found with ID: " + vehicleForm.getVariantId());
	                value.put("success", false);
	                result.add(value);
	                return result;
	            }

	            Vehicles vehicle = new Vehicles();

	            vehicle.setConnectorType(vehicleForm.getConnectorType());
	            vehicle.setVehicleType(vehicleForm.getVehicleType());
	            vehicle.setDescription(vehicleForm.getDescription());
	            vehicle.setRegistrationNo(vehicleForm.getRegistrationNo());
	            vehicle.setUser(user);
	            
	            // Set make, model, year
	            vehicle.setMake(vehicleForm.getMake());
	            vehicle.setModel(vehicleForm.getModel());
	            vehicle.setYear(vehicleForm.getYear());
	            
	            // Set the variant object (not the ID)
	            vehicle.setVariant(variant);  // ✅ CORRECT

	            // VIN optional
	            if (vehicleForm.getVin() != null && !vehicleForm.getVin().trim().isEmpty()) {
	                vehicle.setVin(vehicleForm.getVin());
	            } else {
	                vehicle.setVin("");
	            }

	            // Save vehicle
	            generalDao.save(vehicle);

	            value.put("status", "EV Added Successfully");
	            value.put("success", true);
	            value.put("Make", vehicleForm.getMake());
	            value.put("Model", vehicleForm.getModel());
	            value.put("Variant", variant.getVariantName());  // Get variant name from the object
	            value.put("VariantId", vehicleForm.getVariantId());
	            
	            result.add(value);

	        } catch (Exception e) {
	            e.printStackTrace();
	            value.put("status", "Error adding EV: " + e.getMessage());
	            value.put("success", false);
	            result.add(value);
	        }
	    } else {
	        value.put("status", "User not found with ID: " + vehicleForm.getUserId());
	        value.put("success", false);
	        result.add(value);
	    }

	    return result;
	}

	@Transactional
	@Override
	public List<Vehicles> getMyEVG(long userId) {
	    try {
	        // Use HQL with JOIN FETCH to load variant eagerly
	        String hql = "FROM Vehicles v LEFT JOIN FETCH v.variant WHERE v.user.id = ?1";
	        return generalDao.findAllHQLQry(new Vehicles(), hql, userId);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}

//	@Override
//	public User updateUserDetails(UserDetailsForm usersForm) throws UserNotFoundException, ServerException {
//	    String query = "SELECT * FROM Users WHERE id = " + usersForm.getUserId();
//	    User user = generalDao.findOneSQLQuery(new User(), query);
//
//	    if (user == null) {
//	        throw new ServerException(ErrorCodes.INVALID_USER.toString(),
//	                Integer.toString(ErrorCodes.INVALID_USER.getCode()));
//	    }
//
//	    // --- Uniqueness checks for provided fields (if present) ---
//	    if (usersForm.getUsername() != null) {
//	        String usernameQuery = "SELECT * FROM Users WHERE username = '" + usersForm.getUsername() + "' AND id != " + usersForm.getUserId();
//	        User userByUsername = generalDao.findOneSQLQuery(new User(), usernameQuery);
//	        if (userByUsername != null) {
//	            throw new ServerException("Username already exists. Please use a different username.",
//	                    Integer.toString(ErrorCodes.USERNAME_ALREADY_EXISTS.getCode()));
//	        }
//	        user.setUsername(usersForm.getUsername());
//	    }
//
//	    if (usersForm.getEmail() != null) {
//	        String emailQuery = "SELECT * FROM Users WHERE email = '" + usersForm.getEmail() + "' AND id != " + usersForm.getUserId();
//	        User userByEmail = generalDao.findOneSQLQuery(new User(), emailQuery);
//	        if (userByEmail != null) {
//	            throw new ServerException("Email already exists. Please use a different email.",
//	                    Integer.toString(ErrorCodes.EMAIL_ALREADY_EXISTS.getCode()));
//	        }
//	        user.setEmail(usersForm.getEmail());
//	    }
//
//	    if (usersForm.getMobileNumber() != null) {
//	        String mobileQuery = "SELECT * FROM Users WHERE mobileNumber = '" + usersForm.getMobileNumber() + "' AND id != " + usersForm.getUserId();
//	        User userByMobile = generalDao.findOneSQLQuery(new User(), mobileQuery);
//	        if (userByMobile != null) {
//	            throw new ServerException("Mobile number already exists. Please use a different mobile number.",
//	                    Integer.toString(ErrorCodes.MOBILE_ALREADY_EXISTS.getCode()));
//	        }
//	        user.setMobileNumber(usersForm.getMobileNumber());
//	    }
//
//	    if (usersForm.getFullname() != null) {
//	        if (!usersForm.getFullname().matches("^[A-Za-z ]+$")) {
//	            throw new ServerException("Full name should contain only letters.",
//	                    Integer.toString(ErrorCodes.REG_FIRSTNAME_INVALID.getCode()));
//	        }
//	        user.setFullname(usersForm.getFullname());
//	    }
//
//	    generalDao.savOrupdate(user);
//
//	    // --- Update address only if any address-related fields are given ---
//	    String addressQuery = "SELECT * FROM address WHERE user_id = " + usersForm.getUserId();
//	    Address address = generalDao.findOneSQLQuery(new Address(), addressQuery);
//
//	    if (address != null) {
//	        if (usersForm.getAddress() != null) address.setAddress(usersForm.getAddress());
//	        if (usersForm.getCity() != null) address.setCity(usersForm.getCity());
//	        if (usersForm.getState() != null) address.setState(usersForm.getState());
//	        if (usersForm.getCountry() != null) address.setCountry(usersForm.getCountry());
//	        if (usersForm.getZipCode() != null) address.setZipCode(usersForm.getZipCode());
//
//	        generalDao.savOrupdate(address);
//	    }
//
//	    return user;
//	}
	@Override
	public User updateUserDetails(UserDetailsForm usersForm) throws UserNotFoundException, ServerException {

	    // --- Fetch existing user ---
	    String query = "SELECT * FROM Users WHERE id = " + usersForm.getUserId();
	    User user = generalDao.findOneSQLQuery(new User(), query);

	    if (user == null) {
	        throw new ServerException(ErrorCodes.INVALID_USER.toString(),
	                Integer.toString(ErrorCodes.INVALID_USER.getCode()));
	    }

	    // --- Update only if new value is provided ---
	    if (usersForm.getUsername() != null && !usersForm.getUsername().isEmpty()) {
	        String usernameQuery = "SELECT * FROM Users WHERE username = '" + usersForm.getUsername() 
	                                + "' AND id != " + usersForm.getUserId();
	        User userByUsername = generalDao.findOneSQLQuery(new User(), usernameQuery);
	        if (userByUsername != null) {
	            throw new ServerException("Username already exists. Please use a different username.",
	                    Integer.toString(ErrorCodes.USERNAME_ALREADY_EXISTS.getCode()));
	        }
	        user.setUsername(usersForm.getUsername());
	    }

	    if (usersForm.getEmail() != null && !usersForm.getEmail().isEmpty()) {
	        String emailQuery = "SELECT * FROM Users WHERE email = '" + usersForm.getEmail() 
	                             + "' AND id != " + usersForm.getUserId();
	        User userByEmail = generalDao.findOneSQLQuery(new User(), emailQuery);
	        if (userByEmail != null) {
	            throw new ServerException("Email already exists. Please use a different email.",
	                    Integer.toString(ErrorCodes.EMAIL_ALREADY_EXISTS.getCode()));
	        }
	        user.setEmail(usersForm.getEmail());
	    }

	    
	    if (usersForm.getFullname() != null && !usersForm.getFullname().isEmpty()) {
	        if (!usersForm.getFullname().matches("^[A-Za-z ]+$")) {
	            throw new ServerException("Full name should contain only letters.",
	                    Integer.toString(ErrorCodes.REG_FIRSTNAME_INVALID.getCode()));
	        }
	        user.setFullname(usersForm.getFullname());
	    }

	    // --- Save updated user ---
	    generalDao.savOrupdate(user);

	    // --- Update address if any address fields are provided ---
	    String addressQuery = "SELECT * FROM address WHERE user_id = " + usersForm.getUserId();
	    Address address = generalDao.findOneSQLQuery(new Address(), addressQuery);

	    if (address == null) {
	        // If no existing address, create a new one
	        address = new Address();
	        address.setUser(user);
	    }

	    if (usersForm.getAddress() != null) address.setAddress(usersForm.getAddress());
	    if (usersForm.getCity() != null) address.setCity(usersForm.getCity());
	    if (usersForm.getState() != null) address.setState(usersForm.getState());
	    if (usersForm.getCountry() != null) address.setCountry(usersForm.getCountry());
	    if (usersForm.getZipCode() != null) address.setZipCode(usersForm.getZipCode());
	   

	    generalDao.savOrupdate(address);

	    return user;
	}

	@Override
	public List<Accounts> getWalletDetails(long userId) {
		try {
			return generalDao.findAllSQLQuery(new Accounts(), "SELECT * FROM accounts WHERE user_id = "+userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public List<AccountTransactions> getWalletTransactions(long accId) {
		try {
			return generalDao.findAllSQLQuery(new AccountTransactions(), "SELECT * FROM account_transaction WHERE account_id = "+accId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void deleteVehicleById(Long id) throws UserNotFoundException {
		try {
            // Create a new instance of User to pass as the first argument
            Vehicles vehicleTemplate = new Vehicles();
            Vehicles vehicle = generalDao.findOneById(vehicleTemplate, id);
            
            if (vehicle == null) {
                throw new UserNotFoundException("User not found with id: " + id);
            }
            generalDao.delete(vehicle);
        } catch (Exception e) {
            throw new UserNotFoundException("Failed to delete user: " + e.getMessage(), e);
        }
		
	}
	@Override
	public User getVehicleById(Long id) {
		try {
			// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
			String query = "Select * from Vehicles where id = " + id;
			User net = generalDao.findOneSQLQuery(new User(), query);
			System.out.println("netw :" + net);
			return net;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// Service


	@Override
	@Transactional
	public ResponseMessage updateVehicle(Long vehicleId, Vehicles vehicleForm)
	        throws UserNotFoundException, ServerException {

	    if (vehicleId == null) {
	        throw new ServerException("Vehicle ID must not be null");
	    }

	    Vehicles existingVehicle = generalDao.findOneById(new Vehicles(), vehicleId);
	    if (existingVehicle == null) {
	        throw new UserNotFoundException("Vehicle not found for ID: " + vehicleId);
	    }

	    boolean updated = false;

	    if (vehicleForm.getVin() != null && !vehicleForm.getVin().isEmpty()) {
	        existingVehicle.setVin(vehicleForm.getVin());
	        updated = true;
	    }
	    if (vehicleForm.getYear() != null) {
	        existingVehicle.setYear(vehicleForm.getYear());
	        updated = true;
	    }
	    if (vehicleForm.getMake() != null && !vehicleForm.getMake().isEmpty()) {
	        existingVehicle.setMake(vehicleForm.getMake());
	        updated = true;
	    }
	    if (vehicleForm.getModel() != null && !vehicleForm.getModel().isEmpty()) {
	        existingVehicle.setModel(vehicleForm.getModel());
	        updated = true;
	    }
	    if (vehicleForm.getDescription() != null && !vehicleForm.getDescription().isEmpty()) {
	        existingVehicle.setDescription(vehicleForm.getDescription());
	        updated = true;
	    }
	    if (vehicleForm.getConnectorType() != null && !vehicleForm.getConnectorType().isEmpty()) {
	        existingVehicle.setConnectorType(vehicleForm.getConnectorType());
	        updated = true;
	    }
	    if (vehicleForm.getStatus() != null && !vehicleForm.getStatus().isEmpty()) {
	        existingVehicle.setStatus(vehicleForm.getStatus());
	        updated = true;
	    }
	    if (vehicleForm.getVehicleType() != null && !vehicleForm.getVehicleType().isEmpty()) {
	        existingVehicle.setVehicleType(vehicleForm.getVehicleType());
	        updated = true;
	    }
	    if (vehicleForm.getRegistrationNo() != null && !vehicleForm.getRegistrationNo().isEmpty()) {
	        existingVehicle.setRegistrationNo(vehicleForm.getRegistrationNo());
	        updated = true;
	    }

	    if (!updated) {
	        return new ResponseMessage("No fields provided to update");
	    }

	    generalDao.update(existingVehicle);
	    return new ResponseMessage("Vehicle updated successfully");
	}
	
	
	//////////////////////rfids//////////////////

	 @Override
	   public List<Map<String, Object>> getSingleUserRfid(String userId) {
	       String rfidQuery = "SELECT * FROM rfid WHERE userId = :userId";
	       String requestQuery = "SELECT * FROM rfid_request WHERE userId = :userId";

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
	       if (combined.isEmpty()) {
	           throw new RuntimeException("No RFID found for this userId: " + userId);
	           // OR return Collections.singletonList(Map.of("message", "No RFID found for this userId: " + userId));
	       }

	       return combined;
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
	   	        request.setCreationDate(new Date());
	   	        
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
	   	           // transaction.setCreateTimeStamp(new Date());
	   	            transaction.setTransactionId(generateTransactionId());
	   	            
	   	            generalDao.savOrupdate(transaction);
	   	            
	   	            // 7. Update request status
//	   	            savedRequest.setStatus("APPROVED");
//	   	            return (RFIDRequests) generalDao.savOrupdate(savedRequest);
	   	            
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

	   		@Transactional
	   		@Override
	   	    public Address addOrUpdateAddress(Long userId, Address address) throws Exception {
	   	        User user = entityManager.find(User.class, userId);
	   	        if (user == null) {
	   	            throw new Exception("User not found with id: " + userId);
	   	        }
	   	        String hql = "FROM Address a WHERE a.user.id = :userId AND (a.address IS NULL OR a.address = '')";
	   	        Query query = entityManager.createQuery(hql);
	   	        query.setParameter("userId", userId);
	   	        List result = query.getResultList();
	   	        
	   	        Address addressToSave;
	   	        if (!result.isEmpty()) {
	   	            addressToSave = (Address) result.get(0); // update existing null address
	   	        } else {
	   	            addressToSave = new Address();
	   	            addressToSave.setUser(user);
	   	        }

	   	        addressToSave.setAddress(address.getAddress());
	   	        addressToSave.setCity(address.getCity());
	   	        addressToSave.setState(address.getState());
	   	        addressToSave.setCountry(address.getCountry());
	   	        addressToSave.setZipCode(address.getZipCode());
	   	        addressToSave.setPhone(address.getPhone());

	   	        if (addressToSave.getId() == null) {
	   	            entityManager.persist(addressToSave);
	   	        } else {
	   	            entityManager.merge(addressToSave);
	   	        }
	   	        return addressToSave;
	   	    }
	   		
	   		@Override
	   		public String deleteAddressById(Long addId) throws Exception {
	   			Address address = entityManager.find(Address.class, addId);
	   			if (address == null) {
	   				throw new Exception ("Address not found with id:" + addId);
	   			}
	   			entityManager.remove(address);
	   			return "Address deleted successfully";
	   		}
	   			   		
	   		@Override
	   		public AccountTransactions createPayment(PaymentRequestDTO paymentRequestDTO) throws Exception {
	   		    try {
	   		        System.out.println("=== Processing Payment Request ===");
	   		        System.out.println("Payment DTO: " + paymentRequestDTO);
	   		        
	   		        // Validate required fields
	   		        if (paymentRequestDTO.getUserId() == null) {
	   		            throw new RuntimeException("User ID is required");
	   		        }
	   		        
	   		        if (paymentRequestDTO.getAmtCredit() == null || paymentRequestDTO.getAmtCredit() <= 0) {
	   		            throw new RuntimeException("Payment amount must be greater than 0");
	   		        }

	   		        // Get user using generalDao
	   		        Long userId = paymentRequestDTO.getUserId();
	   		        System.out.println("Looking for user with ID: " + userId);
	   		        
	   		        String userQuery = "SELECT * FROM users WHERE id = " + userId;
	   		        User user = generalDao.findOneSQLQuery(new User(), userQuery);
	   		        
	   		        if (user == null) {
	   		            throw new RuntimeException("User not found with id: " + userId);
	   		        }
	   		        
	   		        System.out.println("User found: " + user.getId());

	   		        // Get or create account for user
	   		        Accounts account = findOrCreateUserAccount(user);
	   		        System.out.println("Account found/created: " + account.getId());

	   		        // Create REAL Razorpay order
	   		        String razorpayOrderId = createRazorpayOrder(paymentRequestDTO.getAmtCredit());
	   		        System.out.println("Real Razorpay Order Created: " + razorpayOrderId);

	   		        // Create AccountTransactions object
	   		        AccountTransactions paymentTransaction = new AccountTransactions();
	   		        paymentTransaction.setAmtCredit(paymentRequestDTO.getAmtCredit());
	   		        paymentTransaction.setAmtDebit(paymentRequestDTO.getAmtDebit() != null ? paymentRequestDTO.getAmtDebit() : 0.0);
	   		        paymentTransaction.setCurrentBalance(paymentRequestDTO.getCurrentBalance() != null ? paymentRequestDTO.getCurrentBalance() : 0.0);
	   		        paymentTransaction.setStatus("CREATED");
	   		        
	   		        // Use REAL Razorpay order ID as transaction ID
	   		        paymentTransaction.setTransactionId(razorpayOrderId);
	   		        
	   		        // Set comment
	   		        paymentTransaction.setComment("Wallet top-up via Razorpay - Amount: " + paymentTransaction.getAmtCredit() + 
	   		                                ", Order ID: " + razorpayOrderId);
	   		        
	   		       // paymentTransaction.setCreateTimeStamp(new Date());
	   		        paymentTransaction.setAccount(account);

	   		        // Calculate current balance (for top-up, add credit amount to current balance)
	   		        double newBalance = account.getAccountBalance() + paymentTransaction.getAmtCredit();
	   		        paymentTransaction.setCurrentBalance(newBalance);

	   		        System.out.println("Saving transaction...");
	   		        // Save the transaction using generalDao
	   		        AccountTransactions savedTransaction = (AccountTransactions) generalDao.save(paymentTransaction);
	   		        
	   		        // Update account balance using generalDao
	   		        account.setAccountBalance(newBalance);
	   		        generalDao.save(account);
	   		        
	   		        System.out.println("Payment transaction created successfully: " + savedTransaction.getId());

	   		        return savedTransaction;
	   		        
	   		    } catch (Exception e) {
	   		        System.err.println("Error in createPayment: " + e.getMessage());
	   		        e.printStackTrace();
	   		        throw new RuntimeException("Failed to create payment: " + e.getMessage());
	   		    }
	   		}

	   		// Method to create actual Razorpay order
	   		private String createRazorpayOrder(Double amount) throws Exception {
	   		    try {
	   		        // Initialize Razorpay client with your keys
	   		        RazorpayClient razorpay = new RazorpayClient("rzp_test_z51wLt0JB2gGzV", "iZYcsJ1YNogZxvx8Qv0lnaje");
	   		        
	   		        // Create order request
	   		        JSONObject orderRequest = new JSONObject();
	   		        orderRequest.put("amount", amount * 100); // Convert to paise
	   		        orderRequest.put("currency", "INR");
	   		        orderRequest.put("receipt", "rcpt_" + System.currentTimeMillis());
	   		        orderRequest.put("payment_capture", 1); // Auto capture
	   		        
	   		        System.out.println("Creating Razorpay order with request: " + orderRequest);
	   		        
	   		        // Create order
	   		        Order order = razorpay.orders.create(orderRequest);
	   		        String orderId = order.get("id");
	   		        
	   		        System.out.println("Razorpay order created successfully: " + orderId);
	   		        return orderId;
	   		        
	   		    } catch (Exception e) {
	   		        System.err.println("Failed to create Razorpay order: " + e.getMessage());
	   		        throw new RuntimeException("Failed to create payment order: " + e.getMessage());
	   		    }
	   		}

	   		// Helper method to find or create user account using generalDao
	   		private Accounts findOrCreateUserAccount(User user) throws Exception {
	   		    // Try to find existing account
	   		    String accountQuery = "SELECT * FROM accounts WHERE user_id = " + user.getId();
	   		    Accounts account = generalDao.findOneSQLQuery(new Accounts(), accountQuery);
	   		    
	   		    if (account != null) {
	   		        System.out.println("Existing account found: " + account.getId());
	   		        return account;
	   		    }
	   		    
	   		    System.out.println("Creating new account for user: " + user.getId());
	   		    // Create new account if not exists
	   		    Accounts newAccount = new Accounts();
	   		    newAccount.setUser(user);
	   		    newAccount.setAccountBalance(0.0);
	   		    newAccount.setCreationDate(new Date());
	   		    
	   		    Accounts savedAccount = (Accounts) generalDao.save(newAccount);
	   		    System.out.println("New account created: " + savedAccount.getId());
	   		    return savedAccount;
	   		}
	   		
	   		@Override
	   		@Transactional(propagation = Propagation.NOT_SUPPORTED)
	   		public String updatePaymentStatus(PaymentRequestDTO callbackDTO) {
	   		    try {
	   		        System.out.println("=== Payment Callback Received in Service ===");
	   		        System.out.println("Callback DTO: " + callbackDTO);
	   		        
	   		        String razorpayOrderId = callbackDTO.getRazorpay_order_id();
	   		        String razorpayPaymentId = callbackDTO.getRazorpay_payment_id();
	   		        String razorpaySignature = callbackDTO.getRazorpay_signature();
	   		        
	   		        System.out.println("Order ID: " + razorpayOrderId);
	   		        System.out.println("Payment ID: " + razorpayPaymentId);
	   		        System.out.println("Signature: " + razorpaySignature);
	   		        
	   		        if (razorpayOrderId == null || razorpayOrderId.isEmpty()) {
	   		            throw new RuntimeException("Razorpay order ID is required");
	   		        }
	   		        
	   		        // Use the working query from your logs
	   		        String query = "SELECT * FROM account_transaction WHERE comment LIKE '%" + 
	   		                      razorpayOrderId.replace("'", "''") + "%'";
	   		        
	   		        AccountTransactions transaction = generalDao.findOneSQLQuery(new AccountTransactions(), query);
	   		        
	   		        if (transaction != null) {
	   		            System.out.println("Transaction found with ID: " + transaction.getId());
	   		            
	   		            // Determine status
	   		            String status = "COMPLETED";
	   		            if (callbackDTO.getError() != null || razorpayPaymentId == null) {
	   		                status = "FAILED";
	   		            }
	   		            
	   		            // Update transaction
	   		            transaction.setStatus(status);
	   		            String comment = "Payment " + status.toLowerCase() + " - Order: " + razorpayOrderId + 
	   		                           ", Payment ID: " + razorpayPaymentId;
	   		            
	   		            if (razorpaySignature != null) {
	   		                comment += ", Signature: " + razorpaySignature;
	   		            }
	   		            
	   		            if (callbackDTO.getError() != null) {
	   		                comment += ", Error: " + callbackDTO.getError();
	   		            }
	   		            
	   		            transaction.setComment(comment);
	   		            generalDao.update(transaction);
	   		            
	   		            System.out.println("Transaction status updated to: " + status);
	   		            return "Payment status updated successfully for order: " + razorpayOrderId + ". Status: " + status;
	   		        } else {
	   		            throw new RuntimeException("Transaction not found for order ID: " + razorpayOrderId);
	   		        }
	   		    } catch (Exception e) {
	   		        System.err.println("Error in updatePaymentStatus: " + e.getMessage());
	   		        throw new RuntimeException("Failed to update payment status: " + e.getMessage());
	   		    }
	   		}
	   		@Override
	   		public String capturePayment(String paymentId, int amount) throws Exception {
	   		    try {
	   		        // Initialize Razorpay client
	   		        this.razorpayClient = new RazorpayClient(razorPayKey, razorpaySecret);
	   		        
	   		        JSONObject captureRequest = new JSONObject();
	   		        captureRequest.put("amount", amount * 100); // Amount in paise
	   		        captureRequest.put("currency", "INR");

	   		        // For actual implementation, uncomment this line:
	   		        // Payment payment = razorpayClient.payments.capture(paymentId, captureRequest);
	   		        
	   		        System.out.println("Payment capture requested - Payment ID: " + paymentId + ", Amount: " + amount);
	   		        
	   		        // For now, return mock success response
	   		        return "Payment captured successfully for payment ID: " + paymentId + ", Amount: ₹" + amount;
	   		    } catch (Exception e) {
	   		        System.err.println("Error in capturePayment: " + e.getMessage());
	   		        throw new RuntimeException("Failed to capture payment: " + e.getMessage());
	   		    }
	   		}

	   		@Override
	   		public String refundPayment(String paymentId, int amount) throws Exception {
	   		    try {
	   		        // Initialize Razorpay client
	   		        this.razorpayClient = new RazorpayClient(razorPayKey, razorpaySecret);
	   		        
	   		        JSONObject refundRequest = new JSONObject();
	   		        refundRequest.put("amount", amount * 100); // Amount in paise
	   		        refundRequest.put("payment_id", paymentId);

	   		        // For actual implementation, uncomment this line:
	   		        // Refund refund = razorpayClient.payments.refund(refundRequest);
	   		        
	   		        System.out.println("Refund requested - Payment ID: " + paymentId + ", Amount: " + amount);
	   		        
	   		        // Create refund transaction record
	   		        String refundQuery = "SELECT * FROM account_transaction WHERE comment LIKE '%" + paymentId + "%'";
	   		        AccountTransactions originalTransaction = generalDao.findOneSQLQuery(new AccountTransactions(), refundQuery);
	   		        
	   		        if (originalTransaction != null) {
	   		            // Create refund transaction
	   		            AccountTransactions refundTransaction = new AccountTransactions();
	   		            refundTransaction.setAmtDebit(amount);
	   		            refundTransaction.setAmtCredit(0);
	   		            refundTransaction.setCurrentBalance(originalTransaction.getCurrentBalance() - amount);
	   		            refundTransaction.setStatus("REFUNDED");
	   		            refundTransaction.setComment("Refund for Payment ID: " + paymentId + ", Amount: ₹" + amount);
	   		           // refundTransaction.setCreateTimeStamp(new Date());
	   		            refundTransaction.setAccount(originalTransaction.getAccount());
	   		            refundTransaction.setTransactionId("REFUND_" + System.currentTimeMillis());
	   		            
	   		            generalDao.save(refundTransaction);
	   		            
	   		            // Update account balance
	   		            Accounts account = originalTransaction.getAccount();
	   		            account.setAccountBalance(account.getAccountBalance() - amount);
	   		            generalDao.save(account);
	   		        }
	   		        
	   		        return "Refund processed successfully for payment ID: " + paymentId + ", Amount: ₹" + amount;
	   		    } catch (Exception e) {
	   		        System.err.println("Error in refundPayment: " + e.getMessage());
	   		        throw new RuntimeException("Failed to process refund: " + e.getMessage());
	   		    }
	   		}

	   		@Override
	   		public AccountTransactions getPaymentByOrderId(String orderId) {
	   		    try {
	   		        System.out.println("Searching for payment with order ID: " + orderId);
	   		        
	   		        if (orderId == null || orderId.trim().isEmpty()) {
	   		            throw new IllegalArgumentException("Order ID cannot be empty");
	   		        }
	   		        
	   		        // Sanitize the input to prevent SQL injection
	   		        String sanitizedOrderId = orderId.replace("'", "''");
	   		        
	   		        AccountTransactions transaction = null;
	   		        
	   		        // Method 1: Try searching in the correct column (based on your previous logs)
	   		        try {
	   		            // Based on your logs, the comment search worked. Let's try that first.
	   		            String query1 = "SELECT * FROM account_transaction WHERE comment LIKE '%" + sanitizedOrderId + "%'";
	   		            transaction = generalDao.findOneSQLQuery(new AccountTransactions(), query1);
	   		            if (transaction != null) {
	   		                System.out.println("Found transaction via comment search: " + transaction.getId());
	   		                return transaction;
	   		            }
	   		        } catch (Exception e) {
	   		            System.out.println("Comment search failed: " + e.getMessage());
	   		        }
	   		        
	   		        // Method 2: Try other possible column names
	   		        String[] possibleColumns = {"transaction_id", "transactionId", "order_id", "orderId", "reference_id", "referenceId"};
	   		        
	   		        for (String column : possibleColumns) {
	   		            try {
	   		                String query = "SELECT * FROM account_transaction WHERE " + column + " = '" + sanitizedOrderId + "'";
	   		                transaction = generalDao.findOneSQLQuery(new AccountTransactions(), query);
	   		                if (transaction != null) {
	   		                    System.out.println("Found transaction via column '" + column + "': " + transaction.getId());
	   		                    return transaction;
	   		                }
	   		            } catch (Exception e) {
	   		                System.out.println("Search in column '" + column + "' failed: " + e.getMessage());
	   		            }
	   		        }
	   		        
	   		        // If still not found, throw appropriate exception
	   		        throw new RuntimeException("Payment transaction not found for order ID: " + orderId);
	   		        
	   		    } catch (RuntimeException e) {
	   		        // Re-throw RuntimeExceptions
	   		        throw e;
	   		    } catch (Exception e) {
	   		        System.err.println("Error in getPaymentByOrderId: " + e.getMessage());
	   		        throw new RuntimeException("Failed to retrieve payment: " + e.getMessage());
	   		    }
	   		}

	   		@Override
	   		public Map<String, Object> calculate(double portCapacity, double pricePerUnit, Map<String, Object> body) {

	   		    Double timeInHours = body.containsKey("timeInHours") ? ((Number) body.get("timeInHours")).doubleValue() : null;
	   		    Double units = body.containsKey("units") ? ((Number) body.get("units")).doubleValue() : null;
	   		    Double amount = body.containsKey("amount") ? ((Number) body.get("amount")).doubleValue() : null;

	   		    Map<String, Object> result = new HashMap<>();
	   		    if (timeInHours != null) {
	   		        units = portCapacity * timeInHours;
	   		        amount = units * pricePerUnit;
	   		    } else if (units != null) {
	   		        timeInHours = units / portCapacity;
	   		        amount = units * pricePerUnit;
	   		    } else if (amount != null) {
	   		        units = amount / pricePerUnit;
	   		        timeInHours = units / portCapacity;
	   		    } else {
	   		        throw new IllegalArgumentException("Please provide one of: timeInHours, units, or amount.");
	   		    }
	   		    result.put("timeInHours", round(timeInHours));
	   		    result.put("units", round(units));
	   		    result.put("amount", round(amount));

	   		    return result;
	   		}

	   		private double round(double value) {
	   		    return Math.round(value * 100.0) / 100.0;
	   		}
	   		
	   		@Override
	   		public List<GetSiteDetailsDto> getAllSiteDetailsMobileapp(
	   		        String powerType,
	   		        String connectorTypes,
	   		        String status,
	   		        String powerRange,
	   		        String priceRange,
	   		        Double latitude,
	   		        Double longitude,
	   		        String search) {

	   		    boolean applyDistance = latitude != null && longitude != null;
	   		    boolean hasHaving = false;

	   		    List<String> powerTypesFilter = split(powerType);
	   		    List<String> connectorTypesFilter = split(connectorTypes);

	   		    Double minPower = parseMin(powerRange);
	   		    Double maxPower = parseMax(powerRange);
	   		    Double minPrice = parseMin(priceRange);
	   		    Double maxPrice = parseMax(priceRange);

	   		    StringBuilder sql = new StringBuilder();

	   		    // ================= SELECT =================
	   		    sql.append("SELECT ")
	   		       .append("s.id AS site_id, ")
	   		       .append("s.siteName AS site_name, ")
	   		       .append("MIN(l.address) AS address, ")
	   		   
	   		 .append("MIN(CAST(l.latitude AS DOUBLE PRECISION)) AS latitude, ")
	   		 .append("MIN(CAST(l.longitude AS DOUBLE PRECISION)) AS longitude, ")


	   		       // POWER TYPES
	   		       .append("(SELECT STRING_AGG(x.power_type, ',') FROM (")
	   		       .append("   SELECT DISTINCT p2.power_type ")
	   		       .append("   FROM port p2 ")
	   		       .append("   JOIN station st2 ON st2.id = p2.station_id ")
	   		       .append("   WHERE st2.site_id = s.id ")
	   		       .append(") x) AS power_types, ")

	   		       // CONNECTOR TYPES
	   		       .append("(SELECT STRING_AGG(y.connector_type, ',') FROM (")
	   		       .append("   SELECT DISTINCT p3.connector_type ")
	   		       .append("   FROM port p3 ")
	   		       .append("   JOIN station st3 ON st3.id = p3.station_id ")
	   		       .append("   WHERE st3.site_id = s.id ")
	   		       .append(") y) AS connector_types, ")

	   		       .append("MIN(p.max_power_kw) AS min_power_kw, ")
	   		       .append("MAX(p.max_power_kw) AS max_power_kw, ")
	   		       .append("MIN(p.billingAmount) AS min_price, ")
	   		       .append("MAX(p.billingAmount) AS max_price, ")
	   		       .append("COUNT(DISTINCT p.id) AS total_ports, ")
	   		       .append("SUM(CASE WHEN LOWER(sn.status) = 'available' THEN 1 ELSE 0 END) AS available_ports ");

	   		    // ================= DISTANCE =================
	   		    if (applyDistance) {
	   		        sql.append(", (6371 * acos(")
	   		           .append("cos(radians(:latitude)) * cos(radians(MIN(l.latitude))) * ")
	   		           .append("cos(radians(MIN(l.longitude)) - radians(:longitude)) + ")
	   		           .append("sin(radians(:latitude)) * sin(radians(MIN(l.latitude)))")
	   		           .append(")) AS distance ");
	   		    }

	   		    // ================= FROM =================
	   		    sql.append("FROM site s ")
	   		       .append("LEFT JOIN site_location l ON l.site_id = s.id ")
	   		       .append("LEFT JOIN station st ON st.site_id = s.id ")
	   		       .append("LEFT JOIN port p ON p.station_id = st.id ")
	   		       .append("LEFT JOIN statusNotification sn ON sn.port_id = p.id ")
	   		       .append("WHERE 1=1 ");

	   		    // ================= SEARCH =================
	   		    if (search != null && !search.trim().isEmpty()) {
	   		        sql.append("AND (LOWER(s.siteName) LIKE LOWER(:search) ")
	   		           .append("OR LOWER(l.address) LIKE LOWER(:search)) ");
	   		    }

	   		    // ================= POWER TYPE =================
	   		    if (!powerTypesFilter.isEmpty()) {
	   		        sql.append("AND EXISTS (")
	   		           .append("SELECT 1 FROM port p1 ")
	   		           .append("JOIN station st1 ON st1.id = p1.station_id ")
	   		           .append("WHERE st1.site_id = s.id ")
	   		           .append("AND p1.power_type IN (:powerTypes)) ");
	   		    }

	   		    // ================= CONNECTOR TYPE =================
	   		    if (!connectorTypesFilter.isEmpty()) {
	   		        sql.append("AND EXISTS (")
	   		           .append("SELECT 1 FROM port p2 ")
	   		           .append("JOIN station st2 ON st2.id = p2.station_id ")
	   		           .append("WHERE st2.site_id = s.id ")
	   		           .append("AND p2.connector_type IN (:connectorTypes)) ");
	   		    }

	   		    // ================= POWER RANGE =================
	   		    if (minPower != null) sql.append("AND p.max_power_kw >= :minPower ");
	   		    if (maxPower != null) sql.append("AND p.max_power_kw <= :maxPower ");

	   		    // ================= PRICE RANGE =================
	   		    if (minPrice != null) sql.append("AND p.billingAmount >= :minPrice ");
	   		    if (maxPrice != null) sql.append("AND p.billingAmount <= :maxPrice ");

	   		    // ================= GROUP BY =================
	   		    sql.append("GROUP BY s.id, s.siteName ");

	   		    // ================= STATUS =================
	   		    if (status != null && !status.trim().isEmpty()) {
	   		        if ("ACTIVE".equalsIgnoreCase(status)) {
	   		            sql.append("HAVING SUM(CASE WHEN LOWER(sn.status)='available' THEN 1 ELSE 0 END) > 0 ");
	   		        } else {
	   		            sql.append("HAVING SUM(CASE WHEN LOWER(sn.status)='available' THEN 1 ELSE 0 END) = 0 ");
	   		        }
	   		        hasHaving = true;
	   		    }

	   		    // ================= DISTANCE FILTER =================
	   		    if (applyDistance) {
	   		        sql.append(hasHaving ? " AND " : " HAVING ")
	   		           .append("(6371 * acos(")
	   		           .append("cos(radians(:latitude)) * cos(radians(MIN(l.latitude))) * ")
	   		           .append("cos(radians(MIN(l.longitude)) - radians(:longitude)) + ")
	   		           .append("sin(radians(:latitude)) * sin(radians(MIN(l.latitude)))")
	   		           .append(")) <= 10 ");

	   		        sql.append("ORDER BY distance ASC ");
	   		    }

	   		    // ================= EXECUTION =================
	   		    Query query = entityManager.createNativeQuery(sql.toString());

	   		    if (applyDistance) {
	   		        query.setParameter("latitude", latitude);
	   		        query.setParameter("longitude", longitude);
	   		    }

	   		    if (!powerTypesFilter.isEmpty()) query.setParameter("powerTypes", powerTypesFilter);
	   		    if (!connectorTypesFilter.isEmpty()) query.setParameter("connectorTypes", connectorTypesFilter);
	   		    if (minPower != null) query.setParameter("minPower", minPower);
	   		    if (maxPower != null) query.setParameter("maxPower", maxPower);
	   		    if (minPrice != null) query.setParameter("minPrice", minPrice);
	   		    if (maxPrice != null) query.setParameter("maxPrice", maxPrice);
	   		    if (search != null && !search.trim().isEmpty()) {
	   		        query.setParameter("search", "%" + search + "%");
	   		    }

	   		    // ================= MAPPING =================
	   		    List<Object[]> rows = query.getResultList();
	   		    List<GetSiteDetailsDto> response = new ArrayList<>();

	   		    for (Object[] row : rows) {
	   		        int i = 0;

	   		     GetSiteDetailsDto dto = new GetSiteDetailsDto();
	   		  dto.setSiteId(((Number) row[i++]).longValue());
	   		  dto.setSiteName((String) row[i++]);
	   		  dto.setAddress((String) row[i++]);

	   		  dto.setLatitude(toDouble(row[i++]));     // FIXED
	   		  dto.setLongitude(toDouble(row[i++]));    // FIXED

	   		  dto.setPowerTypes(row[i] != null
	   		          ? new HashSet<>(Arrays.asList(row[i].toString().split(",")))
	   		          : new HashSet<>());
	   		  i++;

	   		  dto.setConnectorTypes(row[i] != null
	   		          ? new HashSet<>(Arrays.asList(row[i].toString().split(",")))
	   		          : new HashSet<>());
	   		  i++;

	   		Double  minPowerKw = toDouble(row[i++]);
	   		Double  maxPowerKw = toDouble(row[i++]);
	   		Double  minBilling = toDouble(row[i++]);
	   		Double  maxBilling = toDouble(row[i++]);

	   		  long totalPorts = ((Number) row[i++]).longValue();
	   		  long availablePorts = ((Number) row[i++]).longValue();


	   		        dto.setNoOfPorts(totalPorts);
	   		        dto.setAvaiPorts(availablePorts);
	   		        dto.setSiteStatus(availablePorts > 0 ? "ACTIVE" : "INACTIVE");

	   		        if (applyDistance && row.length > i && row[i] != null) {
	   		            dto.setDistanceKm(((Number) row[i]).doubleValue());
	   		        }

	   		     if (minPowerKw != null && maxPowerKw != null) {
	   		      dto.setCapacityRange(
	   		          minPowerKw.equals(maxPowerKw)
	   		              ? String.valueOf(minPowerKw)
	   		              : minPowerKw + "-" + maxPowerKw
	   		      );
	   		  } else {
	   		      dto.setCapacityRange("N/A");
	   		  }

	   		  if (minBilling != null && maxBilling != null) {
	   		    dto.setPriceRange(
	   		        minBilling.equals(maxBilling)
	   		            ? String.valueOf(minBilling)
	   		            : minBilling + "-" + maxBilling
	   		    );
	   		} else {
	   		    dto.setPriceRange("N/A");
	   		}
	   		    response.add(dto);
	   		    }

	   		    return response;
	   		}

	   		// ================= HELPERS =================
	   		private List<String> split(String value) {
	   		    if (value == null || value.isBlank()) return new ArrayList<>();
	   		    return Arrays.stream(value.split(","))
	   		            .map(String::trim)
	   		            .collect(Collectors.toList());
	   		}

	   		private Double parseMin(String range) {
	   		    if (range == null || range.isBlank()) return null;
	   		    return Double.valueOf(range.split("-")[0]);
	   		}

	   		private Double parseMax(String range) {
	   		    if (range == null || range.isBlank()) return null;
	   		    String[] parts = range.split("-");
	   		    return parts.length > 1 ? Double.valueOf(parts[1]) : Double.valueOf(parts[0]);
	   		}

	   		private Double toDouble(Object value) {
	   		    if (value == null) return null;
	   		    if (value instanceof Number) {
	   		        return ((Number) value).doubleValue();
	   		    }
	   		    return Double.valueOf(value.toString());
	   		}

	   		@Override
	   		@Transactional(readOnly = true)
	   		public List<SiteDetailsResponseDto> getSiteDetailsBySiteId(Long siteId) {

	   		    Site site;
	   		    try {
	   		        site = generalDao.findOneById(new Site(), siteId);
	   		    } catch (UserNotFoundException e) {
	   		        return Collections.emptyList();
	   		    }

	   		    if (site == null) {
	   		        return Collections.emptyList();
	   		    }

	   		    SiteDetailsResponseDto response = new SiteDetailsResponseDto();

	   		    response.setSiteId(site.getId());
	   		    response.setSiteName(site.getSiteName());
	   		    response.setOwnerOrgId(site.getOwnerOrg());
	   		    response.setOwnerId(site.getOwnerId());
	   		 response.setManagerName(site.getManagerName());
	   		response.setManagerPhone(site.getManagerPhone());

	   		    /* ---------------- Locations ---------------- */
	   		    List<LocationDto> locations = new ArrayList<>();
	   		    site.getLocation().forEach(loc -> {
	   		        LocationDto dto = new LocationDto(
	   		                loc.getAddress(),
	   		                loc.getLatitude(),
	   		                loc.getLongitude()
	   		        );
	   		        locations.add(dto);
	   		    });
	   		    response.setLocations(locations);

	   		    /* ---------------- Stations ---------------- */
	   		    List<StationsDto> stationDtos = new ArrayList<>();

	   		    int totalPorts = 0;
	   		    int availablePorts = 0;
	   		    double minPower = Double.MAX_VALUE;
	   		    double maxPower = 0;
	   		    double minPrice = Double.MAX_VALUE;
	   		    double maxPrice = 0;
	   		    Set<String> powerTypes = new HashSet<>();

	   		    for (Station station : site.getStation()) {

	   		        StationsDto stationDto = new StationsDto();
	   		        stationDto.setStationId(station.getId());
	   		        stationDto.setStationName(station.getStationName());
	   		        stationDto.setSerialNumber(station.getSerialNo());
	   		        stationDto.setCurrentType(station.getCurrent_type());
	   		        stationDto.setLastHeartbeat(station.getLastHeartBeat());
	   		        stationDto.setStatus(station.getStationStatus());

	   		        List<PortsDto> portDtos = new ArrayList<>();

	   		        for (Port port : station.getPort()) {

	   		            totalPorts++;

	   		            PortsDto portDto = new PortsDto();
	   		            portDto.setPortId(port.getId());
	   		            portDto.setConnectorId(port.getConnectorId());
	   		            portDto.setConnectorName(port.getConnectorName());
	   		            portDto.setConnectorType(port.getConnector_type());
	   		            portDto.setPowerCapacity(port.getMax_power_kW());
	   		            portDto.setPowerType(port.getPower_type());
	   		            portDto.setBillingAmount(port.getBillingAmount());
	   		            portDto.setBillingUnits(port.getBillingUnits());

	   		            // Power & price range
	   		            if (port.getMax_power_kW() != null) {
	   		                minPower = Math.min(minPower, port.getMax_power_kW());
	   		                maxPower = Math.max(maxPower, port.getMax_power_kW());
	   		            }
	   		            if (port.getBillingAmount() != null) {
	   		                minPrice = Math.min(minPrice, port.getBillingAmount());
	   		                maxPrice = Math.max(maxPrice, port.getBillingAmount());
	   		            }
	   		            if (port.getPower_type() != null) {
	   		                powerTypes.add(port.getPower_type());
	   		            }

	   		            /* -------- Status Notifications -------- */
	   		            List<StatusNotificationDtos> statusDtos = new ArrayList<>();

	   		            for (StatusNotification sn : port.getStatusNotifcation()) {

	   		                StatusNotificationDtos sDto =
	   		                        new StatusNotificationDtos(
	   		                                sn.getId(),
	   		                                sn.getStatus(),
	   		                                sn.getLastContactedTime()
	   		                        );

	   		                if ("Available".equalsIgnoreCase(sn.getStatus())) {
	   		                    availablePorts++;
	   		                }

	   		                statusDtos.add(sDto);
	   		            }

	   		            portDto.setStatusNotifications(statusDtos);
	   		            portDtos.add(portDto);
	   		        }

	   		        stationDto.setPorts(portDtos);
	   		        stationDtos.add(stationDto);
	   		    }

	   		    response.setStations(stationDtos);
	   		    response.setTotalStations(stationDtos.size());
	   		    response.setTotalPorts(totalPorts);
	   		    response.setAvailablePorts(availablePorts);

	   		    response.setPower(
	   		            minPower == Double.MAX_VALUE ? "0-0" : minPower + "-" + maxPower
	   		    );
	   		    response.setPrice(
	   		            minPrice == Double.MAX_VALUE ? "0-0" : minPrice + "-" + maxPrice
	   		    );
	   		    response.setPowerType(String.join(", ", powerTypes));

	   		    return Collections.singletonList(response);
	   		}


	}
