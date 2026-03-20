package com.ewe.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ewe.dao.GeneralDao;
import com.ewe.exception.DuplicateUserException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.FranchiseOwnerDetailsDTO;
import com.ewe.form.ReportDto;
import com.ewe.form.UserDetailsForm;
import com.ewe.pojo.Accounts;
import com.ewe.pojo.Address;
import com.ewe.pojo.ChargingActivity;
import com.ewe.pojo.Employee;
import com.ewe.pojo.Owner_Orgs;
import com.ewe.pojo.Password;
import com.ewe.pojo.RFID;
import com.ewe.pojo.RFIDRequests;
import com.ewe.pojo.User;
import com.ewe.pojo.Usersinroles;
import com.ewe.pojo.WhiteLabelOrgs;
import com.ewe.pojo.users_in_owners;
import com.ewe.pojo.users_in_whitelabels;
import com.ewe.service.EmailService;
import com.ewe.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private GeneralDao<?, ?> generalDao;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailservice;
	@PersistenceContext
	private EntityManager entityManager;
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	@Value("$admin.email")
	private String Adminemail;
	@Value("$admin.name")
	private String AdminName;

	private final Map<Long, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<Long, Long> otpCreationTime = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TokenData> resetTokens = new ConcurrentHashMap<>();

    private static final long TOKEN_EXPIRY_MS = 5 * 60 * 1000;
    private static final long OTP_VALIDITY_DURATION = 5 * 60 * 1000; // 5 minutes in millisecond
	
    @Override
	public void addDriver(UserDetailsForm userForm) throws UserNotFoundException, DuplicateUserException {
		User existingUserByUsername = findByUserName(userForm.getUsername());
	    if (existingUserByUsername != null) {
	        throw new DuplicateUserException("Username '" + userForm.getUsername() + "' already exists");
	    }

	    try {
	        User existingUserByEmail = getUserByEmail(userForm.getEmail());
	        if (existingUserByEmail != null) {
	            throw new DuplicateUserException("Email '" + userForm.getEmail() + "' already exists");
	        }
	    } catch (UserNotFoundException e) {
	        // Email not found is okay, we can proceed
	    }
		try {
			User user = new User();
			user.setUsername(userForm.getUsername());
			user.setEmail(userForm.getEmail());
			user.setFullname(userForm.getFullname());
			user.setMobileNumber(userForm.getMobileNumber());
			user.setEnabled(true);
			if (userForm.getOrgId() != null) {
				user.setOrgId(userForm.getOrgId());
			}
			generalDao.save(user);

			Address address = new Address();
			address.setAddress(userForm.getAddress());
			address.setCity(userForm.getCity());
			address.setCountry(userForm.getCountry());
			address.setPhone(userForm.getMobileNumber());
			address.setState(userForm.getState());
			address.setZipCode(userForm.getZipCode());
			address.setUser(user);
			generalDao.save(address);

			Password password = new Password();
			String encodedPassword = passwordEncoder.encode(userForm.getPassword());
			password.setPassword(encodedPassword);
			password.setUser(user);
			generalDao.save(password);

			Accounts account = new Accounts();
			account.setAccountBalance(0);
			account.setUser(user);
			account.setCreationDate(new Date());
			generalDao.save(account);
			
			RFID rfid=new  RFID();
            rfid.setUserId(user.getId());
	        rfid.setPhone(user.getMobileNumber());
	        generalDao.save(rfid);

			updatingRoleId("Driver", user.getId(), user);
			try {
			    emailservice.sendEmailWithTemplate(user.getEmail(), user.getFullname());
			} catch (Exception e) {
			    logger.error("Failed to send welcome email to " + user.getEmail(), e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updatingRoleId(String role, Long id, User users) throws UserNotFoundException {
		String query = "SELECT id FROM role WHERE roleName ='" + role + "'";
		long value = generalDao.findIdBySqlQuery(query);

		Usersinroles user = new Usersinroles();
		user.setRole_id(value);
		user.setUser(users);
		generalDao.save(user);

	}

	@Override
	public User getProfileById(Long id) {
		try {
			// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
			String query = "Select * from Users where id = " + id;
			User net = generalDao.findOneSQLQuery(new User(), query);
			System.out.println("netw :" + net);
			return net;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void addWhiteLabel(UserDetailsForm userForm) throws UserNotFoundException, DuplicateUserException {
		User existingUserByUsername = findByUserName(userForm.getUsername());
	    if (existingUserByUsername != null) {
	        throw new DuplicateUserException("Username '" + userForm.getUsername() + "' already exists");
	    }

	    // Check if email already exists
	    try {
	        User existingUserByEmail = getUserByEmail(userForm.getEmail());
	        if (existingUserByEmail != null) {
	            throw new DuplicateUserException("Email '" + userForm.getEmail() + "' already exists");
	        }
	    } catch (UserNotFoundException e) {
	        // Email not found is okay, we can proceed
	    }
	    
	    try {
	        User existingUserByMobile = getUserByMobileNumber(userForm.getMobileNumber());
	        if (existingUserByMobile != null) {
	            throw new DuplicateUserException("Mobile number '" + userForm.getMobileNumber() + "' already exists");
	        }
	    } catch (UserNotFoundException e) {
	    	
	    }
	    
	    try {
	        WhiteLabelOrgs existingOrg = getWhiteLabelOrgByName(userForm.getOrgName());
	        if (existingOrg != null) {
	            throw new DuplicateUserException("WhiteLabel organization name '" + userForm.getOrgName() + "' already exists");
	        }
	    } catch (UserNotFoundException e) {
	        // ok, not found — can continue
	    }

		User user = new User();
		user.setUsername(userForm.getUsername());
		user.setEmail(userForm.getEmail());
		user.setFullname(userForm.getFullname());
		user.setMobileNumber(userForm.getMobileNumber());
		user.setEnabled(true);
		generalDao.save(user);

		Address address = new Address();
		address.setAddress(userForm.getAddress());
		address.setCity(userForm.getCity());
		address.setCountry(userForm.getCountry());
		address.setPhone(userForm.getMobileNumber());
		address.setState(userForm.getState());
		address.setZipCode(userForm.getZipCode());
		address.setUser(user);
		generalDao.save(address);

		Password password = new Password();
		String encodedPassword = passwordEncoder.encode(userForm.getPassword());
		password.setPassword(encodedPassword);
		password.setUser(user);
		generalDao.save(password);

		WhiteLabelOrgs orgDetails = new WhiteLabelOrgs();
		orgDetails.setOrgName(userForm.getOrgName());
		orgDetails.setFlag(false);
		generalDao.save(orgDetails);

		users_in_whitelabels usrs_orgs = new users_in_whitelabels();
		usrs_orgs.setUser_id(user.getId());
		usrs_orgs.setWl_org_id(orgDetails.getId());
		generalDao.save(usrs_orgs);

		updatingRoleId("WhiteLabel", user.getId(), user);
		try {
		    emailservice.sendEmailWithTemplate(user.getEmail(), user.getFullname());
		} catch (Exception e) {
		    // Log the error but don't fail the registration
		    logger.error("Failed to send welcome email to " + user.getEmail(), e);
		}
	   }
		 
		@Override
		public boolean emailExists(String email) {

		    try {
		        String sql = "SELECT * FROM users WHERE email = '" + email + "'";
		        User user = generalDao.findOneSQLQuery(new User(), sql);

		        if (user != null) {
		            String token = UUID.randomUUID().toString();
		            resetTokens.put(email, new TokenData(token));
		            String resetLink = "http://localhost:8800/reset-password?token=" + token + "&email=" + email;
		            emailservice.sendPasswordResetEmail(
		                email,
		                user.getFullname(),
		                resetLink
		            );
		        }
		        return user != null;
		    } catch (Exception e) {
		        logger.error("Error in emailExists for: " + email, e);
		        return false;
		    }
	   }

	@Override
	public void addFranchiseOwner(UserDetailsForm userForm) throws UserNotFoundException, DuplicateUserException {
		User existingUserByUsername = findByUserName(userForm.getUsername());
	    if (existingUserByUsername != null) {
	        throw new DuplicateUserException("Username '" + userForm.getUsername() + "' already exists");
	    }
	    try {
	        User existingUserByEmail = getUserByEmail(userForm.getEmail());
	        if (existingUserByEmail != null) {
	            throw new DuplicateUserException("Email '" + userForm.getEmail() + "' already exists");
	        }
	    } catch (UserNotFoundException e) {
	    }
	    try {
	        User existingUserByMobile = getUserByMobileNumber(userForm.getMobileNumber());
	        if (existingUserByMobile != null) {
	            throw new DuplicateUserException("Mobile number '" + userForm.getMobileNumber() + "' already exists");
	        }
	    } catch (UserNotFoundException e) {
	    	
	    }
	    try {
	        Owner_Orgs existingOrg = getOrgByName(userForm.getOrgName());
	        if (existingOrg != null) {
	            throw new DuplicateUserException("Organization name '" + userForm.getOrgName() + "' already exists");
	        }
	    } catch (UserNotFoundException e) {

	    }

	    User user = new User();
	    user.setUsername(userForm.getUsername());
	    user.setEmail(userForm.getEmail());
	    user.setFullname(userForm.getFullname());
	    user.setMobileNumber(userForm.getMobileNumber());
	    user.setEnabled(true);
	    generalDao.save(user);

	    Address address = new Address();
	    address.setAddress(userForm.getAddress());
	    address.setCity(userForm.getCity());
	    address.setCountry(userForm.getCountry());
	    address.setPhone(userForm.getMobileNumber());
	    address.setState(userForm.getState());
	    address.setZipCode(userForm.getZipCode());
	    address.setUser(user);
	    generalDao.save(address);

	    Password password = new Password();
	    String encodedPassword = passwordEncoder.encode(userForm.getPassword());
	    password.setPassword(encodedPassword);
	    password.setUser(user);
	    generalDao.save(password);

	    Owner_Orgs orgs = new Owner_Orgs();
	    orgs.setOrgName(userForm.getOrgName());
	    orgs.setWhitelabelId(userForm.getOrgId());
	    orgs.setDriverFranchise(false);
	    generalDao.save(orgs);

	    users_in_owners user_orgs = new users_in_owners();
	    user_orgs.setOwner_org_id(orgs.getId());
	    user_orgs.setUser_id(user.getId());
	    generalDao.save(user_orgs);
        updatingRoleId("FranchiseOwner", user.getId(), user);
	    try {
	        emailservice.sendEmailWithTemplate(user.getEmail(), user.getFullname());
	    } catch (Exception e) {
	        logger.error("Failed to send welcome email to " + user.getEmail(), e);
	    }
	}

	@Override
	public User getUser(String username) throws UserNotFoundException {
		String query = "Select * from Users   WHERE username = '" + username + "' Or email ='" + username + "'";
		System.out.println(query);
		User user = generalDao.findOneSQLQuery(new User(), query);
		System.out.println(user);
		return user;
	}	

	@Override
	public List<Map<String, Object>> getAllWhiteLabels() throws UserNotFoundException {
	    return getAllWhiteLabels(null); // Call the new method with null search
	}

	@Override
	public List<Map<String, Object>> getAllWhiteLabels(String search) throws UserNotFoundException {
	    String baseQuery = "SELECT u.id as userId, u.fullname, u.email, u.mobilenumber, wl.orgName, wl.id as orgId " +
	                      "FROM Users u " +
	                      "INNER JOIN usersinroles ur ON u.id = ur.user_id " +
	                      "INNER JOIN users_in_whitelabels uw ON u.id = uw.user_id " +
	                      "INNER JOIN white_lable_orgs wl ON wl.id = uw.wl_org_id " +
	                      "WHERE ur.role_id = 3";
	    
	    // Add search condition only if search term is provided
	    if (search != null && !search.trim().isEmpty()) {
	        baseQuery += " AND (u.fullname LIKE '%" + search + "%' " +
	                    "OR u.email LIKE '%" + search + "%' " +
	                    "OR wl.orgName LIKE '%" + search + "%' " +
	                    "OR u.mobilenumber LIKE '%" + search + "%')";
	    }
	    return generalDao.getMapData(baseQuery);
	}
	
	 @Override
	    public List<User> getAllDrivers(Long orgId) throws UserNotFoundException {
	        return getAllDrivers(orgId, null); // Calls the new implementation with null search
	    }

	 @Override
	 public List<User> getAllDrivers(Long orgId, String search) throws UserNotFoundException {
	     // 1. Build base query
	     StringBuilder query = new StringBuilder(
	         "SELECT u.* FROM Users u " +
	         "INNER JOIN usersinroles ur ON u.id = ur.user_id " +
	         "WHERE ur.role_id = 2"
	     );

	     // 2. Add filters
	     if (orgId != null && orgId != 1) {
	         query.append(" AND u.orgId = :orgId");
	     }
	     
	     if (search != null && !search.trim().isEmpty()) {
	         query.append(" AND (LOWER(u.fullname) LIKE :search OR " +
	                     "LOWER(u.email) LIKE :search OR " +
	                     "u.mobileNumber LIKE :search OR " +
	                     "LOWER(u.username) LIKE :search)");
	     }

	     // 3. Execute query with parameters
	     Map<String, Object> params = new HashMap<>();
	     if (orgId != null && orgId != 1) {
	         params.put("orgId", orgId);
	     }
	     if (search != null && !search.trim().isEmpty()) {
	    	    params.put("search", "%" + search.toLowerCase() + "%");
	    	}
	    	List<User> users = generalDao.findAllSQLQuery(new User(), query.toString(), params);

	     // 4. Fetch organization names using getMapData (available in your interface)
	     if (users != null && !users.isEmpty()) {
	         Set<Long> orgIds = users.stream()
	             .map(User::getOrgId)
	             .filter(Objects::nonNull)
	             .collect(Collectors.toSet());

	         if (!orgIds.isEmpty()) {
	             String orgQuery = "SELECT id, orgName FROM white_lable_orgs WHERE id IN (" + 
	                 orgIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
	             
	             // Using getMapData which is available in your interface
	             List<Map<String, Object>> orgResults = generalDao.getMapData(orgQuery);
	             Map<Long, String> orgMap = orgResults.stream()
	                 .collect(Collectors.toMap(
	                     org -> ((Number) org.get("id")).longValue(),
	                     org -> (String) org.get("orgName")
	                 ));
	             users.forEach(user -> {
	                 if (user.getOrgId() != null) {
	                     user.setOrgName(orgMap.get(user.getOrgId()));
	                 }
	             });
	         }
	     }
	     return users;
	 }

	@Override
	public WhiteLabelOrgs getOrgById(Long id) {
		try {
			// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
			String query = "Select * from white_lable_orgs where id = " + id;
			WhiteLabelOrgs net = generalDao.findOneSQLQuery(new WhiteLabelOrgs(), query);
			System.out.println("netw :" + net);
			return net;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void updateUser(Long id, UserDetailsForm user) throws UserNotFoundException {

		User userdetails = getProfileById(id);
		if (userdetails != null) {
			userdetails.setFullname(user.getFullname());
			userdetails.setMobileNumber(user.getMobileNumber());
			generalDao.savOrupdate(userdetails);
			Address address = userdetails.getAddress().iterator().next();
			address.setAddress(user.getAddress());
			address.setCountry(user.getCountry());
			address.setState(user.getState());
			address.setCity(user.getCity());
			address.setZipCode(user.getZipCode());
			generalDao.savOrupdate(address);

			if (user.isPasswordchange()) {
				Password password = userdetails.getPasswords().iterator().next();
				password.setPassword(user.getPassword());
				generalDao.savOrupdate(password);
			}
		}
	}

	@Override
	public List<WhiteLabelOrgs> getwhiteLabels() throws UserNotFoundException {
		String Query = "SELECT * FROM white_lable_orgs";

		List<WhiteLabelOrgs> user = generalDao.findAllSQLQuery(new WhiteLabelOrgs(), Query);
		return user;
	}

	@Override
	public List<Map<String, Object>> getAllOwners(Long id) throws UserNotFoundException {
	    return getAllOwners(id, null); // Call the new method with null search
	}
	@Override
	public List<Map<String, Object>> getAllOwners(Long id, String search) throws UserNotFoundException {
	    String query = "SELECT " +
	            "    u.id AS userId, " +
	            "    u.fullname, " +
	            "    u.email, " +
	            "    u.mobilenumber, " +
	            "    u.username AS username," +
	            "    MIN(oo.orgName) AS owner_orgName, " +
	            "    MIN(oo.id) AS owner_orgId, " +
	            "    MIN(wl.orgName) AS wl_orgName, " +
	            "    MIN(wl.id) AS wl_orgId " +
	            "FROM Users u " +
	            "LEFT JOIN usersinroles ur ON u.id = ur.user_id AND ur.role_id = 4 " +
	            "LEFT JOIN users_in_owners uo ON u.id = uo.user_id " +
	            "LEFT JOIN owner_orgs oo ON oo.id = uo.Owner_org_id " +
	            "LEFT JOIN white_lable_orgs wl ON wl.id = oo.whitelabelId " +
	            "WHERE ur.role_id = 4 ";

	    // Add whitelabel filter if id is provided and not equal to 1
	    if (id != null && id != 1) {
	        query += "AND oo.whitelabelId = " + id + " ";
	    }

	    // Add search condition if search term is provided
	    if (search != null && !search.trim().isEmpty()) {
	        String searchTerm = search.toLowerCase();
	        query += "AND (LOWER(u.fullname) LIKE '%" + searchTerm + "%' " +
	                "OR LOWER(u.email) LIKE '%" + searchTerm + "%' " +
	                "OR u.mobilenumber LIKE '%" + searchTerm + "%' " +
	                "OR LOWER(u.username) LIKE '%" + searchTerm + "%' " +
	                "OR LOWER(oo.orgName) LIKE '%" + searchTerm + "%' " +
	                "OR LOWER(wl.orgName) LIKE '%" + searchTerm + "%') ";
	    }

	    query += "GROUP BY u.id, u.fullname, u.email, u.mobilenumber, u.username;";

	    System.out.println("Executing query: " + query); // For debugging
	    return generalDao.getMapData(query);
	}
	@Override
	public List<Owner_Orgs> getOwners(Long id) throws UserNotFoundException {
	    StringBuilder query = new StringBuilder("SELECT * FROM owner_orgs");
	    
	    // Add WHERE clause only if id is provided and not equal to 1
	    if (id != null && id != 1) {
	        query.append(" WHERE whitelabelId = ").append(id);
	    }
	    // If id is null or 1, return all records without filtering
	    
	    return generalDao.findAllSQLQuery(new Owner_Orgs(), query.toString());
	}
	
	@Override
	public User getUserByEmail(String email) throws UserNotFoundException {
	    String sql = "SELECT * FROM users WHERE email = '" + email + "'";
	    User user = generalDao.findOneSQLQuery(new User(), sql);
	    if (user == null) {
	        throw new UserNotFoundException("User not found with email: " + email);
	    }
	    return user;
	}
	
	@Override
	public User getUserByMobileNumber(String mobileNumber) throws UserNotFoundException {
	    String sql = "SELECT * FROM users WHERE mobileNumber = '" + mobileNumber + "'";
	    User user = generalDao.findOneSQLQuery(new User(), sql);
	    if (user == null) {
	        throw new UserNotFoundException("User not found with mobile number: " + mobileNumber);
	    }
	    return user;
	}
	
	@Override
	public Owner_Orgs getOrgByName(String orgName) throws UserNotFoundException {
	    String sql = "SELECT * FROM owner_orgs WHERE orgName = '" + orgName + "'";
	    Owner_Orgs org = generalDao.findOneSQLQuery(new Owner_Orgs(), sql);
	    if (org == null) {
	        throw new UserNotFoundException("Organization not found with name: " + orgName);
	    }
	    return org;
	}
	
	@Override
	public WhiteLabelOrgs getWhiteLabelOrgByName(String orgName) throws UserNotFoundException {
    String sql = "SELECT * FROM white_lable_orgs WHERE orgName = '" + orgName + "'";
    WhiteLabelOrgs org = generalDao.findOneSQLQuery(new WhiteLabelOrgs(), sql);
    if (org == null) {
        throw new UserNotFoundException("WhiteLabel org not found: " + orgName);
    }
    return org;
    }

	@Override
	public boolean savePassword(String email, String password) throws UserNotFoundException {
	    User user = getUserByEmail(email);

	    // Check for previous usage
	    if (isPasswordInHistory(user, password)) {
	        throw new IllegalArgumentException("Cannot use a previously used password");
	    }

	    // Fetch latest password entity (if exists)
	    Optional<Password> existingPassword = user.getPasswords().stream()
	        .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
	        .findFirst();

	    if (existingPassword.isPresent()) {
	        // ✅ Update existing password record
	        Password pwd = existingPassword.get();
	        pwd.setPassword(passwordEncoder.encode(password));
	        pwd.setCreatedAt(LocalDateTime.now());
	        generalDao.update(pwd);
	    } else {
	        // ✅ No password exists yet — create a new one
	        Password newPassword = new Password();
	        newPassword.setPassword(passwordEncoder.encode(password));
	        newPassword.setCreatedAt(LocalDateTime.now());
	        newPassword.setUser(user);
	        generalDao.save(newPassword);
	    }

	    // Update user's last update time
	    user.setUpdatedAt(LocalDateTime.now());
	    generalDao.update(user);

	    try {
	        emailservice.sendPasswordUpdateSuccessEmail(user.getEmail(), user.getFullname(), user.getUsername());
	        logger.info("Password updated successfully for user: {}", email);
	        return true;
	    } catch (MessagingException e) {
	        logger.error("Failed to send success email to: {}", email, e);
	        return true;
	    }
	}
	
	private boolean isPasswordInHistory(User user, String newPassword) {
		return user.getPasswords().stream()
				.anyMatch(p -> passwordEncoder.matches(newPassword, p.getPassword()));
		
}	
	
	    @Override
	    public void deleteUserById(Long userId) throws UserNotFoundException {
	        try {
	            // Create a new instance of User to pass as the first argument
	            User userTemplate = new User();
	            User user = generalDao.findOneById(userTemplate, userId);
	            
	            if (user == null) {
	                throw new UserNotFoundException("User not found with id: " + userId);
	            }
	            generalDao.delete(user);
	        } catch (Exception e) {
	            throw new UserNotFoundException("Failed to delete user: " + e.getMessage(), e);
	        }
	    }

	    @Override
	    public List<FranchiseOwnerDetailsDTO> getFranchiseOwnerDetails(Long wlOrgId) {
	        String sql = "SELECT o.id AS org_id, o.orgName, u.fullname, u.email, u.mobileNumber, u.username, u.id " +
	                     "FROM owner_orgs o " +
	                     "JOIN users_in_owners uio ON o.id = uio.owner_org_id " +
	                     "JOIN Users u ON uio.user_id = u.id " +
	                     "WHERE o.whitelabelId = :wlOrgId AND o.driverFranchise = 0";

	        Query query = generalDao.createNativeQuery(sql);
	        query.setParameter("wlOrgId", wlOrgId);

	        List<Object[]> resultList = query.getResultList();
	        List<FranchiseOwnerDetailsDTO> dtoList = new ArrayList<>();

	        for (Object[] row : resultList) {
	            FranchiseOwnerDetailsDTO dto = new FranchiseOwnerDetailsDTO();
	            dto.setOrgId(((Number) row[0]).longValue());      // o.id
	            dto.setOrgName((String) row[1]);                   // o.orgName
	            dto.setFullName((String) row[2]);                  // u.fullname
	            dto.setEmail((String) row[3]);                      // u.email
	            dto.setMobileNumber((String) row[4]);              // u.mobileNumber
	            dto.setUsername((String) row[5]);                   // u.username
	            dto.setUserId(((Number) row[6]).longValue());      // u.id
	            dtoList.add(dto);
	        }

	        return dtoList;
	    }
		@Override
		public List<ReportDto> generateReport(Long siteId, Long stationId, LocalDate startDate, LocalDate endDate) {

		    StringBuilder jpql = new StringBuilder("SELECT ca FROM ChargingActivity ca WHERE 1=1");
		    Map<String, Object> params = new HashMap<>();

		    if (siteId != null) {
		        jpql.append(" AND ca.siteId = :siteId");
		        params.put("siteId", siteId);
		    }

		    if (stationId != null) {
		        jpql.append(" AND ca.stationId = :stationId");
		        params.put("stationId", stationId);
		    }

		    if (startDate != null && endDate != null) {
		        jpql.append(" AND ca.startTime BETWEEN :startDateTime AND :endDateTime");
		        params.put("startDateTime", startDate.atStartOfDay());
		        params.put("endDateTime", endDate.atTime(23, 59, 59));
		    }

		    List<ChargingActivity> results =
		            generalDao.findByQuery(jpql.toString(), params, ChargingActivity.class);

		    return results.stream().map(this::mapToDto).collect(Collectors.toList());
		}

		private ReportDto mapToDto(ChargingActivity ca) {
		    ReportDto dto = new ReportDto();
		    dto.setSiteName(ca.getSitename());
		    dto.setStationName(ca.getStationName());
		    //dto.setPortName(ca.getPortName());
		    dto.setKwConsumption(ca.getKwConsuption());
		    dto.setEnergyDelivered(ca.getEnergyDelivered());
		    dto.setRevenue(ca.getRevenue());
		    dto.setLocation(ca.getLocation());
		    
		    // Assuming activityDate is used as both start and end for daily report entries
		    dto.setStartTime(ca.getStartTime());
		    dto.setEndTime(ca.getEndTime());
		    return dto;
		}		
		
		@Override
		public User findByUserName(String username) throws UserNotFoundException {
		    try {
		        // Create and execute query to find user by username
		        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username");
		        query.setParameter("username", username);
		        query.setMaxResults(1);
		        
		        @SuppressWarnings("unchecked")
		        List<User> users = query.getResultList();
		        
		        if (users.isEmpty()) {
		            return null;
		        }
		        return users.get(0);
		    } catch (Exception e) {
		        throw new UserNotFoundException("Error finding user by username: " + username, e);
		    }
		}
		private static class TokenData {
		
		    String token;
		    long expiryTime;		
		    TokenData(String token) {
		        this.token = token;
		        this.expiryTime = System.currentTimeMillis() + TOKEN_EXPIRY_MS;
		    }	
		    boolean isValid() {
		        return System.currentTimeMillis() < expiryTime;		    
         }
        }
		
		@Override
		public Employee getEmployeeByUsername(String username) {
		    try {
		        String sql = "SELECT * FROM employee WHERE username = :username";
		        Map<String, Object> params = new HashMap<>();
		        params.put("username", username);

		        List<Employee> result = generalDao.findAllSQLQuery(new Employee(), sql, params);

		        if (!result.isEmpty()) {
		            return result.get(0);
		        }
		        return null; // or throw exception if preferred
		    } catch (Exception e) {
		        logger.error("Error fetching employee by username: " + username, e);
		        return null;
		    }
		}
		
		@Override
		@Transactional
		public void updateUserStatus(Long userId, boolean enabled) throws UserNotFoundException {

		    User user = getProfileById(userId);

		    if (user == null) {
		        throw new UserNotFoundException("User not found with id: " + userId);
		    }

		    // Avoid unnecessary DB update
		    if (user.isEnabled() == enabled) {
		        return;
		    }

		    user.setEnabled(enabled);
		    user.setUpdatedAt(LocalDateTime.now());

		    generalDao.update(user);
		}
		
		
		@Override
public Map<String, Object> getFranchiseWithWhiteLabel(Long franchiseId) throws UserNotFoundException {

		    String sql = "SELECT " +
		            "o.id AS franchiseId, " +
		            "o.orgName AS franchiseName, " +
		            "o.whitelabelId AS whiteLabelId, " +
		            "w.orgName AS whiteLabelName " +
		            "FROM owner_orgs o " +
		            "LEFT JOIN white_lable_orgs w ON o.whitelabelId = w.id " +
		            "WHERE o.id = " + franchiseId;

		    System.out.println("Executing SQL: " + sql);
		    
		    try {
		        Map<String, Object> result = generalDao.getSingleMapData(sql);
		        System.out.println("FRANCHISE QUERY RESULT: " + result);
		        System.out.println("Result class: " + (result != null ? result.getClass().getName() : "null"));
		        System.out.println("Result size: " + (result != null ? result.size() : 0));
		        
		        if (result != null && !result.isEmpty()) {
		            System.out.println("Keys in result: " + result.keySet());
		            System.out.println("franchiseId value: " + result.get("franchiseId"));
		            System.out.println("franchiseName value: " + result.get("franchiseName"));
		            System.out.println("whiteLabelId value: " + result.get("whiteLabelId"));
		            System.out.println("whiteLabelName value: " + result.get("whiteLabelName"));
		        }

		        if (result == null) {
		            System.out.println("Result is null, creating empty HashMap");
		            result = new HashMap<>();
		        } else if (result.isEmpty()) {
		            System.out.println("Result is empty for franchiseId: " + franchiseId);
		        }

		        return result;
		    } catch (Exception e) {
		        System.out.println("Error executing query: " + e.getMessage());
		        e.printStackTrace();
		        return new HashMap<>();
		    }
		}
		}