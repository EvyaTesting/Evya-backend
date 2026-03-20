package com.ewe.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ewe.dao.GeneralDao;
import com.ewe.exception.DuplicateUserException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.EmployeeDTO;
import com.ewe.form.IssuesDto;
import com.ewe.pojo.Employee;
import com.ewe.pojo.IssueReporting;
import com.ewe.pojo.Password;
import com.ewe.pojo.User;
import com.ewe.pojo.WhiteLabelOrgs;
import com.ewe.service.EmailService;
import com.ewe.service.EmployeeService;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

@Autowired
private GeneralDao<?, ?> generalDao;
@Autowired
private PasswordEncoder passwordEncoder;
@Autowired
private EmailService emailservice;
@PersistenceContext
private EntityManager entityManager;

private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
private static final ConcurrentHashMap<String, TokenData> resetTokens = new ConcurrentHashMap<>();

private static final long TOKEN_EXPIRY_MS = 5 * 60 * 1000;
private static final long OTP_VALIDITY_DURATION = 5 * 60 * 1000;

@Override
public void addEmployee(EmployeeDTO dto) throws DuplicateUserException, UserNotFoundException {

	String emailCheckQuery = "SELECT * FROM employee WHERE email = '" + dto.getEmail() + "'";
   Employee existingEmail = generalDao.findOneSQLQuery(new Employee(), emailCheckQuery);
   if (existingEmail != null) {
       throw new DuplicateUserException("Email '" + dto.getEmail() + "' already exists");
   }

   String usernameCheckQuery = "SELECT * FROM employee WHERE username = '" + dto.getUsername() + "'";
   Employee existingUser = generalDao.findOneSQLQuery(new Employee(), usernameCheckQuery);
   if (existingUser != null) {
       throw new DuplicateUserException("Username '" + dto.getUsername() + "' already exists");
   }

   Employee emp = new Employee();
   emp.setDesignation(dto.getDesignation());
   emp.setJoiningDate(dto.getJoiningDate());
   emp.setUsername(dto.getUsername());
   emp.setEmail(dto.getEmail());
   emp.setMobileNumber(dto.getMobileNumber());
   emp.setLocation(dto.getLocation());
   emp.setActive(dto.isActive());

   generalDao.save(emp);
 
   try {
       emailservice.sendEmailWithTemplate(dto.getEmail(), dto.getUsername());
   } catch (Exception e) {
       logger.error("Failed to send employee welcome email", e);
   }
}

@Override
public void updateEmployee(Long id, EmployeeDTO employeeDTO) throws UserNotFoundException {
    try {
        Employee employee = generalDao.findOneById(new Employee(), id);
        if (employee == null) {
            throw new UserNotFoundException("Employee not found with id: " + id);
        }

        if (employeeDTO.getUsername() != null) employee.setUsername(employeeDTO.getUsername());
        if (employeeDTO.getFullName() != null) employee.setFullname(employeeDTO.getFullName());
        if (employeeDTO.getMobileNumber() != null) employee.setMobileNumber(employeeDTO.getMobileNumber());
        if (employeeDTO.getEmail() != null) employee.setEmail(employeeDTO.getEmail());
        if (employeeDTO.getDesignation() != null) employee.setDesignation(employeeDTO.getDesignation());
        if (employeeDTO.getLocation() != null) employee.setLocation(employeeDTO.getLocation());

        // Always update active
        employee.setActive(employeeDTO.isActive());

        generalDao.savOrupdate(employee);
        entityManager.flush();

    } catch (Exception e) {
        logger.error("Error updating employee with id: " + id, e);
        throw new UserNotFoundException("Error updating employee", e);
    }
}

public List<Employee> getAllEmployees() {
    return generalDao.findAll(new Employee());
}

@Override
public void deleteEmployee(Long id) throws UserNotFoundException {
    // find employee
    Employee employee = generalDao.findOneById(new Employee(), id);
    if (employee == null) {
        throw new UserNotFoundException("Employee not found with id: " + id);
    }
    // delete employee
    generalDao.delete(employee);
    logger.info("Employee deleted successfully with id: {}", id);
}

@Override
public void deleteAllEmployees() throws UserNotFoundException {
    // fetch all employees
    List<Employee> employees = generalDao.findAll(new Employee());
    if (employees == null || employees.isEmpty()) {
        throw new UserNotFoundException("No employees found to delete");
    }
    // delete each
    for (Employee emp : employees) {
        generalDao.delete(emp);
    }
    logger.info("All employees deleted successfully");
}

@Override
public List<Employee> getEmployeesByDesignation(String designation) throws UserNotFoundException {
    String sql = "SELECT * FROM employee WHERE designation = :designation";
    Map<String, Object> params = new HashMap<>();
    params.put("designation", designation);

    List<Employee> employees = generalDao.findAllSQLQuery(new Employee(), sql, params);
    if (employees == null || employees.isEmpty()) {
        throw new UserNotFoundException("No employees found with designation: " + designation);
    }
    return employees;
}

@Override
public Employee getEmployeeById(Long id) throws UserNotFoundException {
    Employee employee = generalDao.findOneById(new Employee(), id);
    if (employee == null) {
        throw new UserNotFoundException("Employee not found with id: " + id);
    }
    return employee;
}
@Override
public List<IssueReporting> getIssuesByEmployeeId(Long employeeId) throws UserNotFoundException {
    // Check if employee exists
    Employee employee = generalDao.findOneById(new Employee(), employeeId);
    if (employee == null) {
        throw new UserNotFoundException("Employee not found with id: " + employeeId);
    }

    // Fetch issues assigned to this employee
    String sql = "SELECT * FROM issue_reporting WHERE employee_id = :empId";
    Map<String, Object> params = new HashMap<>();
    params.put("empId", employeeId);

    List<IssueReporting> issues = generalDao.findAllSQLQuery(new IssueReporting(), sql, params);

    // Return empty list if no issues
    if (issues == null) {
        issues = new ArrayList<>();
    }
    return issues;
}

@Override
public void updateIssueStatus(Long issueId, String status) throws UserNotFoundException {
    // Fetch the issue
    IssueReporting issue = generalDao.findOneById(new IssueReporting(), issueId);
    if (issue == null) {
        throw new UserNotFoundException("Issue not found with id: " + issueId);
    }
    issue.setStatus(status);
    generalDao.savOrupdate(issue);
}

@Override
public void updateIssuePriority(Long issueId, String priority) throws UserNotFoundException {
    // Fetch the issue
    IssueReporting issue = generalDao.findOneById(new IssueReporting(), issueId);
    if (issue == null) {
        throw new UserNotFoundException("Issue not found with id: " + issueId);
    }
    issue.setPriority(priority);
    generalDao.savOrupdate(issue);
}

@Override
public List<IssuesDto> getResolvedIssues() throws UserNotFoundException {
    String sql = "SELECT * FROM issue_reporting WHERE status = :status";
    Map<String, Object> params = new HashMap<>();
    params.put("status", "Resolved");

    List<IssueReporting> issues = generalDao.findAllSQLQuery(new IssueReporting(), sql, params);

    if (issues == null || issues.isEmpty()) {
        throw new UserNotFoundException("No resolved issues found");
    }

    // Convert to simplified DTO
    List<IssuesDto> dtoList = new ArrayList<>();
    for (IssueReporting issue : issues) {
        IssuesDto dto = new IssuesDto();
        dto.setId(issue.getId());
        dto.setIssue(issue.getIssue());
        dto.setComment(issue.getComment());
        dto.setStatus(issue.getStatus());
        dto.setPriority(issue.getPriority());
        dtoList.add(dto);
    }
    return dtoList;
}

@Override
public List<IssuesDto> getResolvedIssuesByEmployeeId(Long employeeId) throws UserNotFoundException {
    // 1. Check if employee exists
    Employee employee = generalDao.findOneById(new Employee(), employeeId);
    if (employee == null) {
        throw new UserNotFoundException("Employee not found with id: " + employeeId);
    }

    // 2. Fetch resolved issues for this employee
    String sql = "SELECT * FROM issue_reporting WHERE employee_id = :empId AND status = :status";
    Map<String, Object> params = new HashMap<>();
    params.put("empId", employeeId);
    params.put("status", "Resolved");

    List<IssueReporting> issues = generalDao.findAllSQLQuery(new IssueReporting(), sql, params);

    // 3. Return empty list if none found
    if (issues == null) {
        issues = new ArrayList<>();
    }

    // 4. Convert to DTO
    List<IssuesDto> dtoList = new ArrayList<>();
    for (IssueReporting issue : issues) {
        IssuesDto dto = new IssuesDto();
        dto.setId(issue.getId());
        dto.setTicketId(issue.getTicketId());
        dto.setIssue(issue.getIssue());
        dto.setComment(issue.getComment());
        dto.setStatus(issue.getStatus());
        dto.setPriority(issue.getPriority());

        dtoList.add(dto);
    }
    return dtoList;
}

//for pagination added by pavan.....
@Override
public List<Employee> getAllEmployeesFiltered(String designation, String search) {
  StringBuilder hql = new StringBuilder("FROM Employee e WHERE 1=1");
  Map<String, Object> params = new HashMap<>();

  if (designation != null && !designation.isEmpty()) {
      hql.append(" AND e.designation = :designation");
      params.put("designation", designation);
  }

  if (search != null && !search.isEmpty()) {
  	hql.append(" AND (LOWER(e.username) LIKE :search " +
  	           "OR LOWER(e.email) LIKE :search " +
  	           "OR LOWER(e.mobileNumber) LIKE :search " +
  	           "OR LOWER(e.location) LIKE :search)");

      params.put("search", "%" + search.toLowerCase() + "%");
  }

  return generalDao.findByHQL(hql.toString(), params);
}

@Override
public boolean emailExists(String email) {

    try {
        String sql = "SELECT * FROM employee WHERE email = '" + email + "'";
        Employee emp = generalDao.findOneSQLQuery(new Employee(), sql);

        if (emp != null) {
            String token = UUID.randomUUID().toString();
            resetTokens.put(email, new TokenData(token));
            String resetLink = "http://localhost:5173/set-password?token=" + token + "&email=" + email;
            emailservice.sendPasswordResetEmail(
                email,
                emp.getFullname(), // user's name
                resetLink
            );
        }
        return emp != null;
    } catch (Exception e) {
        logger.error("Error in emailExists for: " + email, e);
        return false;
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
public boolean savePassword(String email, String password) throws UserNotFoundException {
    Employee emp = getEmpByEmail(email);

    if (isPasswordInHistory(emp, password)) {
        throw new IllegalArgumentException("Cannot use a previously used password");
    }

    Optional<Password> existingPassword = emp.getPasswords().stream()
        .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
        .findFirst();

    if (existingPassword.isPresent()) {
        Password pwd = existingPassword.get();
        pwd.setPassword(passwordEncoder.encode(password));
        pwd.setCreatedAt(LocalDateTime.now());
        generalDao.update(pwd);
    } else {
        Password newPassword = new Password();
        newPassword.setPassword(passwordEncoder.encode(password));
        newPassword.setCreatedAt(LocalDateTime.now());
        newPassword.setEmployee(emp);
        generalDao.save(newPassword);
    }

    emp.setUpdatedAt(LocalDateTime.now());
    generalDao.update(emp);

    try {
        emailservice.sendPasswordUpdateSuccessEmail(emp.getEmail(), emp.getFullname(), emp.getUsername());
        logger.info("Password updated successfully for user: {}", email);
        return true;
	    } catch (MessagingException e) {
	        logger.error("Failed to send success email to: {}", email, e);
	        return true;
	    }
	}
	
	@Override
	public Employee getEmpByEmail(String email) throws UserNotFoundException {
	    String sql = "SELECT * FROM employee WHERE email = '" + email + "'";
	    Employee emp = generalDao.findOneSQLQuery(new Employee(), sql);
	    if (emp == null) {
	        throw new UserNotFoundException("User not found with email: " + email);
	    }
	    return emp;
	}
	
	private boolean isPasswordInHistory(Employee emp, String newPassword) {
		return emp.getPasswords().stream()
				.anyMatch(p -> passwordEncoder.matches(newPassword, p.getPassword()));
		}
	
	@Override
	public Employee getProfileById(Long id) {
		try {
			// NetworkProfile net = generalDao.findOneById(new NetworkProfile(), id);
			String query = "Select * from Employee where id = " + id;
			Employee net = generalDao.findOneSQLQuery(new Employee(), query);
			System.out.println("netw :" + net);
			return net;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}