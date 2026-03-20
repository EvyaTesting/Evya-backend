package com.ewe.controller;
	
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ewe.exception.UserNotFoundException;
import com.ewe.messages.ResponseMessage;
import com.ewe.service.EmailService;
import com.ewe.service.ManufacturerService;
import com.ewe.service.MobileAPIService;
import com.ewe.service.SiteService;
import com.ewe.service.StationService;
import com.ewe.service.UserService;
import io.swagger.annotations.ApiOperation;
	
@RestController
@RequestMapping("/service/delete")
public class DeletionController {
		@Autowired
		private final UserService userService;
		@Autowired
	    private final ManufacturerService manufacturerService;
		@Autowired
	    private final SiteService siteService;
		@Autowired
	    private final StationService stationService;
		@Autowired
		private final EmailService emailService;
		@Autowired
		private final MobileAPIService mobileService;
	
	    public DeletionController(UserService userService, ManufacturerService manufacturerService, SiteService siteService, StationService stationService,EmailService emailService) {
	        this.userService = userService;
	        this.manufacturerService = manufacturerService;
	        this.siteService = siteService;
	        this.stationService = stationService;
			this.emailService = emailService;
			this.mobileService = null;
	    }
	    
	    private final Map<String, String> otpStorage = new ConcurrentHashMap<>(); 
	    @RequestMapping(value = "/generateDeleteOTP/{role}/{id}", method = RequestMethod.POST)
	    public ResponseEntity<ResponseMessage> generateDeleteOTP(@PathVariable String role, @PathVariable Long id) {
	        try {
	            String otp = generateOTP();
	            String key = generateOTPKey(role, id);
	            otpStorage.put(key, otp);

	            emailService.sendOTPEmail(otp); // Implement this to send OTP

	            return ResponseEntity.ok(new ResponseMessage("OTP sent to admin email"));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(new ResponseMessage("Error generating OTP: " + e.getMessage()));
	        }
	    }

	    private String generateOTP() {
	        int otp = new Random().nextInt(9000) + 1000;
	        return String.valueOf(otp);
	    }

	    private String generateOTPKey(String role, Long id) {
	        return role.toLowerCase().trim() + "_" + id;
	    }

	    @ApiOperation(value = "Verify OTP and Delete Entity")
	    @RequestMapping(value = "/verifyAndDelete/{role}/{id}", method = RequestMethod.POST)
	    public ResponseEntity<ResponseMessage> verifyAndDeleteEntity(
	            @PathVariable String role,
	            @PathVariable Long id,
	            @RequestParam String userEnteredOTP) {

	        String key = generateOTPKey(role, id);
	        String storedOtp = otpStorage.get(key);

	        if (storedOtp == null || !storedOtp.equals(userEnteredOTP)) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(new ResponseMessage("Invalid or expired OTP"));
	        }
	        otpStorage.remove(key);

	        try {
	            switch (role.toLowerCase().trim()) {
	                case "user":
	                    userService.deleteUserById(id);
	                    break;
	                case "manufacturer":
	                    manufacturerService.deleteManufacturerById(id);
	                    break;
	                case "site":
	                    siteService.deleteSiteById(id);
	                    break;
	                case "station":
	                    try{
	                    	stationService.deleteStationById(id);
	                    }catch (IllegalStateException e) {
	                        return ResponseEntity.status(HttpStatus.CONFLICT)
	                                .body(new ResponseMessage(e.getMessage()));
	                    }
	                    break;
	                case "vehicle":
	                	mobileService.deleteVehicleById(id);
	                	break;
	            
	                default:
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                            .body(new ResponseMessage("Invalid role specified"));
	        }
	            return ResponseEntity.ok(new ResponseMessage(role + " deleted successfully"));
	        
	        } catch (UserNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(new ResponseMessage(e.getMessage()));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(new ResponseMessage("Deletion failed: " + e.getMessage()));
	        }
	    }
	}
