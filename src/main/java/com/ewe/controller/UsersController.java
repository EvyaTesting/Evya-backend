package com.ewe.controller;
	
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ewe.exception.DuplicateUserException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.FranchiseOwnerDetailsDTO;
import com.ewe.form.ReportDto;
import com.ewe.form.UserDetailsForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.Owner_Orgs;
import com.ewe.pojo.User;
import com.ewe.pojo.WhiteLabelOrgs;
import com.ewe.service.ManufacturerService;
import com.ewe.service.UserService;
import com.ewe.serviceImpl.TicketServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Hidden;

	@RestController
	@Scope("request")
	@Hidden
	@Api(tags = "UsersController")
	@RequestMapping("/services/userprofile")
	public class UsersController {
		
		@Autowired
		private UserService userService;
		@Autowired
		private ManufacturerService  manufacturerService;
		 private  static final  Logger LOGGER = LoggerFactory.getLogger(TicketServiceImpl.class);
	
		@ApiOperation(value = "Add User Based on Role")
		@RequestMapping(value = "/add", method = RequestMethod.POST)
		public ResponseEntity<ResponseMessage> addUser(@RequestBody(required = false) UserDetailsForm userForm)
				throws UserNotFoundException, DuplicateUserException {
	
			//logger.debug("UserProfileController.addUser() - with [" + userForm.getFirstName() + "]");
			//userProfileService.addUser(userForm);
			
			String role = "";
			if(userForm.getRolename().equalsIgnoreCase("Driver")) {
				userService.addDriver(userForm);
				role = "EV-User";
			}else if(userForm.getRolename().equalsIgnoreCase("WhiteLabel")) {
				userService.addWhiteLabel(userForm);
				role = "WhiteLabel";
			}else if(userForm.getRolename().equalsIgnoreCase("FranchiseOwner")) {
				userService.addFranchiseOwner(userForm);
				role = "FranchiseOwner";
			}else
				role = "Admin";			
			String msg = role + " Successfully Created";
			return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(msg));
		}
		
		@ApiOperation(value = "Get profile By Id")
		@RequestMapping(value = "/userDetails/{id}", method = RequestMethod.GET)
		public ResponseEntity<User> getProfileById(@PathVariable Long id)
				throws UserNotFoundException, InterruptedException {
	
			//logger.info("LoadManagementController.getProfileById() -  [" + id + "]");
			return ResponseEntity.status(HttpStatus.OK).body(userService.getProfileById(id));
		}
		
		@ApiOperation(value = "Update User by UserId")
		@RequestMapping(value = "updateUser/{id}", method = RequestMethod.PUT)
		public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDetailsForm user)
				throws Exception {
			
			userService.updateUser(id, user);
			return ResponseEntity.status(HttpStatus.OK).body(userService.getProfileById(id));
		}
		
		@ApiOperation(value = "Get profile By Id")
		@RequestMapping(value = "/userOrgDetails/{id}", method = RequestMethod.GET)
		public ResponseEntity<WhiteLabelOrgs> getOrgById(@PathVariable Long id)
				throws UserNotFoundException, InterruptedException {
	
			//logger.info("LoadManagementController.getProfileById() -  [" + id + "]");
			return ResponseEntity.status(HttpStatus.OK).body(userService.getOrgById(id));
		}
		
		@ApiOperation(value = "WhiteLabel List")
		@RequestMapping(value = "/whitelabelList", method = RequestMethod.GET)
		public ResponseEntity<Map<String, Object>> getAllWhiteLabels(
		        @RequestParam(defaultValue = "0") int page,
		        @RequestParam(defaultValue = "10") int size,
		        @RequestParam(required = false) String search) throws UserNotFoundException {
		    
		    List<Map<String,Object>> allUsers = userService.getAllWhiteLabels(search);		    
		    int start = page * size;
		    int end = Math.min(start + size, allUsers.size());
		    
		    if (start >= allUsers.size() && allUsers.size() > 0) {
		        start = 0;
		        end = Math.min(size, allUsers.size());
		    }		    
		    List<Map<String,Object>> pagedSites = start < end ? allUsers.subList(start, end) : new ArrayList<>();
		    
		    Map<String, Object> response = new HashMap<>();
		    response.put("whitelabels", pagedSites);
		    response.put("currentPage", page);
		    response.put("totalItems", allUsers.size());
		    response.put("totalPages", (int) Math.ceil((double) allUsers.size() / size));
		    
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		@ApiOperation(value = "Driver List based on pagination, org ID and search")
		@GetMapping("/driverList")
		public ResponseEntity<Map<String, Object>> getAllDrivers(
		        @RequestParam(defaultValue = "0") int page,
		        @RequestParam(defaultValue = "10") int size,
		        @RequestParam(required=false) Long orgId,
		        @RequestParam(required=false) String search) throws UserNotFoundException {

		    List<User> allUsers = userService.getAllDrivers(orgId, search);
		    int start = page * size;
		    int end = Math.min(start + size, allUsers.size());

		    if (start >= allUsers.size() && allUsers.size() > 0) {
		        start = 0;
		        end = Math.min(size, allUsers.size());
		    }
		    List<User> pagedList = start < end ? allUsers.subList(start, end) : new ArrayList<>();

		    Map<String, Object> response = new HashMap<>();
		    response.put("driversList", pagedList);
		    response.put("currentPage", page);
		    response.put("totalItems", allUsers.size());
		    response.put("totalPages", (int) Math.ceil((double) allUsers.size() / size));

		    return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		@ApiOperation(value = "Owner List based on pagination")
		@RequestMapping(value = "/ownerList", method = RequestMethod.GET)
		public ResponseEntity<Map<String, Object>> getAllOwners(
		        @RequestParam(defaultValue = "0") int page,
		        @RequestParam(defaultValue = "10") int size,
		        @RequestParam(required = false) Long id ,
			@RequestParam(required=false)String search)throws UserNotFoundException {
		    
		    List<Map<String,Object>> allUsers = userService.getAllOwners(id,search);
		    
		    int start = page * size;
		    int end = Math.min(start + size, allUsers.size());		    
		    if (start >= allUsers.size() && allUsers.size() > 0) {
		        start = 0;
		        end = Math.min(size, allUsers.size());
		    }
		    
		    List<Map<String,Object>> pagedSites = start < end ? allUsers.subList(start, end) : new ArrayList<>();
		    Map<String, Object> response = new HashMap<>();
		    response.put("ownersList", pagedSites);
		    response.put("currentPage", page);
		    response.put("totalItems", allUsers.size());
		    response.put("totalPages", (int) Math.ceil((double) allUsers.size() / size));
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		@ApiOperation(value = "Get whitelabel profiles  ")
		@RequestMapping(value = "/getwhiteLabels", method = RequestMethod.GET)
		public ResponseEntity<List<WhiteLabelOrgs>> getwhiteLabels()
				throws UserNotFoundException, InterruptedException {
	
			//logger.info("LoadManagementController.getProfileById() -  [" + id + "]");
			return ResponseEntity.status(HttpStatus.OK).body(userService.getwhiteLabels());
		}
		
		@ApiOperation(value = "Get franchise profiles with optional whitelabel filtering")
		@RequestMapping(value = "/getOwners", method = RequestMethod.GET)
		public ResponseEntity<List<Owner_Orgs>> getOwners(
		        @RequestParam(required = false) Long id)  // id=1 for all, otherwise whitelabelId
		        throws UserNotFoundException, InterruptedException {
		    return ResponseEntity.status(HttpStatus.OK).body(userService.getOwners(id));
		}
		
		@ApiOperation(value = "Check if email exists")
	    @RequestMapping(value = "/check-email", method = RequestMethod.GET)
	    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam @Email String email) {
	        Map<String, Object> response = new HashMap<>();
	        try {
	            boolean exists = userService.emailExists(email);
	            response.put("status", exists ? "Reset password link sent" : "error");
	            response.put("message", exists ? "Email exists" : "Email not found");
	            response.put("exists", exists);
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            LOGGER.error("Error checking email existence", e);
	            response.put("status", "error");
	            response.put("message", "Error checking email");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }
	    }
		
	    @ApiOperation(value = "Save the password")
	    @RequestMapping(value = "/save-password", method = RequestMethod.POST)
	    public ResponseEntity<Map<String, Object>> savePassword(
	            @RequestParam @Email String email,
	            @RequestParam @Size(min = 8) String password,
	            @RequestParam String confirmPassword) {
	
	        Map<String, Object> response = new HashMap<>();
	        if (!password.equals(confirmPassword)) {
	            response.put("status", "error");
	            response.put("message", "Passwords do not match");
	            return ResponseEntity.badRequest().body(response);
	        }	
	        if (password.length() < 8) {
	            response.put("status", "error");
	            response.put("message", "Password must be at least 8 characters");
	            return ResponseEntity.badRequest().body(response);
	        }
	
	        try {
	            boolean result = userService.savePassword(email, password);
	            if (result) {
	                response.put("status", "success");
	                response.put("message", "Password updated successfully");
	                return ResponseEntity.ok(response);
	            } else {
	                response.put("status", "error");
	                response.put("message", "Failed to update password");
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	            }
	        } catch (UserNotFoundException e) {
	            response.put("status", "error");
	            response.put("message", "User not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        } catch (IllegalArgumentException e) {
	            response.put("status", "error");
	            response.put("message", e.getMessage());
	            return ResponseEntity.badRequest().body(response);
	        } catch (Exception e) {
	           LOGGER.error("Error updating password for email: {}", email, e);
	            response.put("status", "error");
	            response.put("message", "Internal server error");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }}
	    
	    @ApiOperation(value = "Based on Org Id get the  frachiese data")
	    @RequestMapping(value = "/OwnerDetailsbyOrg/{id}", method = RequestMethod.GET)
	    public ResponseEntity<List<FranchiseOwnerDetailsDTO>> getFranchiseOwnersByOrg(@PathVariable Long id) {
		    List<FranchiseOwnerDetailsDTO> owners = userService.getFranchiseOwnerDetails(id);
		    return ResponseEntity.ok(owners);
		}
	    
	    @ApiOperation(value = "get the Reports data")
	    @RequestMapping(value = "/getreport", method = RequestMethod.GET)
	    public List<ReportDto> getReport(
	            @RequestParam(required = false) Long siteId,
	            @RequestParam(required = false) Long stationId,
	            @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
	        
	        return userService.generateReport(siteId,stationId,startDate,endDate);
	    }
	    
	    @ApiOperation(value = "Update User Status (Active / Inactive)")
	    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.PUT)
	    public ResponseEntity<ResponseMessage> updateUserStatus(
	            @PathVariable Long id,
	            @RequestParam boolean enabled) throws UserNotFoundException {

	        userService.updateUserStatus(id, enabled);

	        String msg = enabled ? "User Activated Successfully" : "User Deactivated Successfully";
	        return ResponseEntity.ok(new ResponseMessage(msg));
	    }
	    
	    @ApiOperation(value = "Get Franchise and WhiteLabel Details By Franchise Id")
	    @RequestMapping(value = "/ownerWithWhiteLabel/{id}", method = RequestMethod.GET)
	    public ResponseEntity<Map<String, Object>> getFranchiseWithWhiteLabel(@PathVariable Long id)
	            throws UserNotFoundException {

	        Map<String, Object> data = userService.getFranchiseWithWhiteLabel(id);

	        return ResponseEntity.status(HttpStatus.OK).body(data);
	    }
	   
	}