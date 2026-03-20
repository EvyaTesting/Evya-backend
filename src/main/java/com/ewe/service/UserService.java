package com.ewe.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ewe.exception.DuplicateUserException;
import com.ewe.exception.InvalidOTPException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.FranchiseOwnerDetailsDTO;
import com.ewe.form.ReportDto;
import com.ewe.form.UserDetailsForm;
import com.ewe.pojo.Employee;
import com.ewe.pojo.Owner_Orgs;
import com.ewe.pojo.User;
import com.ewe.pojo.WhiteLabelOrgs;

public interface UserService {

	void addDriver(UserDetailsForm userForm) throws UserNotFoundException, DuplicateUserException;

	User getProfileById(Long id);

	void addWhiteLabel(UserDetailsForm userForm) throws UserNotFoundException, DuplicateUserException;

	void addFranchiseOwner(UserDetailsForm userForm) throws UserNotFoundException, DuplicateUserException;

	User getUser(String email) throws UserNotFoundException;	

	List<User> getAllDrivers(Long orgId) throws UserNotFoundException;

	WhiteLabelOrgs getOrgById(Long id);

	void updateUser(Long id, UserDetailsForm user) throws UserNotFoundException;

	List<WhiteLabelOrgs> getwhiteLabels() throws UserNotFoundException;

	List<Map<String, Object>> getAllOwners(Long id, String search) throws UserNotFoundException;

	List<Owner_Orgs> getOwners(Long id) throws UserNotFoundException;

	boolean emailExists(String email);

	boolean savePassword(String email, String password) throws UserNotFoundException;

	void deleteUserById(Long id) throws UserNotFoundException;
	
	List<FranchiseOwnerDetailsDTO> getFranchiseOwnerDetails(Long id);
	
	List<ReportDto> generateReport(Long siteId, Long stationId, LocalDate startDate, LocalDate endDate);

	User getUserByEmail(String email) throws UserNotFoundException;
	
	User getUserByMobileNumber(String mobileNumber) throws UserNotFoundException;
	
	User findByUserName(String username) throws UserNotFoundException;
	
	Owner_Orgs getOrgByName(String orgName) throws UserNotFoundException;
	
	WhiteLabelOrgs getWhiteLabelOrgByName(String orgName) throws UserNotFoundException;
	
	 List<Map<String, Object>> getAllWhiteLabels() throws UserNotFoundException;
	    
	    List<Map<String, Object>> getAllWhiteLabels(String search) throws UserNotFoundException;

		List<Map<String, Object>> getAllOwners(Long id) throws UserNotFoundException;

		List<User> getAllDrivers(Long orgId, String search) throws UserNotFoundException;

		Employee getEmployeeByUsername(String username);
		void updateUserStatus(Long userId, boolean enabled) throws UserNotFoundException;
		Map<String, Object> getFranchiseWithWhiteLabel(Long franchiseId) throws UserNotFoundException;

	}
	

