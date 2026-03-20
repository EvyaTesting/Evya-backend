package com.ewe.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.ewe.form.EmployeeDTO;
import com.ewe.form.IssuesDto;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.Employee;
import com.ewe.pojo.IssueReporting;
import com.ewe.service.EmployeeService;
import com.ewe.serviceImpl.TicketServiceImpl;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/services/employee")
public class EmployeeController {

@Autowired
private EmployeeService empService;

private  static final  Logger LOGGER = LoggerFactory.getLogger(TicketServiceImpl.class);

   @ApiOperation(value = "Add Employee (Technician, Support, etc.)")
   @RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
   public ResponseEntity<ResponseMessage> addEmployee(@RequestBody EmployeeDTO dto) {
       try {
           empService.addEmployee(dto);
           return ResponseEntity.status(HttpStatus.CREATED)
               .body(new ResponseMessage("Employee added successfully"));
       } catch (DuplicateUserException e) {
           return ResponseEntity.status(HttpStatus.CONFLICT)
               .body(new ResponseMessage(e.getMessage()));
       } catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(new ResponseMessage("Server error: " + e.getMessage()));
       }
   }

   @ApiOperation(value = "Get all employees")
   @RequestMapping(value = "/getAllEmployees", method = RequestMethod.GET)
   public ResponseEntity<List<Employee>> getAllEmployees() {
       try {
           List<Employee> employees = empService.getAllEmployees();
           return ResponseEntity.ok(employees);
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
       }
   }
   
   @ApiOperation(value = "Update Employee Details")
   @RequestMapping(value = "/updateEmployee/{id}", method = RequestMethod.PUT)
   public ResponseEntity<ResponseMessage> updateEmployee(
           @PathVariable Long id,
           @RequestBody EmployeeDTO dto) throws DuplicateUserException {
       try {
           empService.updateEmployee(id, dto);
           return ResponseEntity.ok()
               .body(new ResponseMessage("Employee updated successfully"));
       } catch (UserNotFoundException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
               .body(new ResponseMessage(e.getMessage()));
       } catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(new ResponseMessage("Error updating employee: " + e.getMessage()));
       }
   }
   
   @ApiOperation(value = "Delete Employee By ID")
   @RequestMapping(value = "/deleteEmployee/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseMessage> deleteEmployee(@PathVariable Long id) {
	    try {
	        empService.deleteEmployee(id);
	        return ResponseEntity.ok(new ResponseMessage("Employee deleted successfully with id: " + id));
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Error deleting employee: " + e.getMessage()));
	    }
	}
   
   @ApiOperation(value = "Delete All Employees")
   @RequestMapping(value = "/deleteAllEmployees", method = RequestMethod.DELETE)
   public ResponseEntity<ResponseMessage> deleteAllEmployees() throws UserNotFoundException {
    try {
        empService.deleteAllEmployees();
        return ResponseEntity.ok(new ResponseMessage("All employees deleted successfully"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ResponseMessage("Error deleting employees: " + e.getMessage()));
    }
   }
   
   @ApiOperation(value = "Get Employees by designation name")
   @RequestMapping(value = "/empByDesignation", method = RequestMethod.GET)
	public List<Employee> getEmployeesByDesignation(@RequestParam String designation) throws UserNotFoundException {
	    return empService.getEmployeesByDesignation(designation);
	}
	
   @ApiOperation(value = "Get Employee By ID")
   @RequestMapping(value = "/getEmployee/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
	    try {
	        Employee employee = empService.getEmployeeById(id);
	        return ResponseEntity.ok(employee);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(new ResponseMessage(e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Error fetching employee: " + e.getMessage()));
	    }
	}
	
	@ApiOperation(value = "Get Issues assigned to an Employee by ID")
	@RequestMapping(value = "/issues/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getIssuesByEmployee(@PathVariable Long id) {
	    try {
	        List<IssueReporting> issues = empService.getIssuesByEmployeeId(id);
	        return ResponseEntity.ok(issues);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(new ResponseMessage(e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Error fetching issues: " + e.getMessage()));
	    }
	}
	
	@ApiOperation(value = "Update Issue Status")
	@RequestMapping(value = "/updateIssueStatus/{issueId}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> updateIssueStatus(
	        @PathVariable Long issueId,
	        @RequestParam String status) {
	    try {
	        empService.updateIssueStatus(issueId, status);
	        return ResponseEntity.ok(new ResponseMessage("Issue status updated to " + status));
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(new ResponseMessage(e.getMessage()));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Error updating issue status: " + e.getMessage()));
	    }
	}
	
	@ApiOperation(value = "Update Issue Priority")
	@RequestMapping(value = "/updateIssuePriority/{issueId}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseMessage> updateIssuePriority(
	        @PathVariable Long issueId,
	        @RequestParam String priority) {
	    try {
	        empService.updateIssuePriority(issueId, priority);
	        return ResponseEntity.ok(new ResponseMessage("Issue priority updated to " + priority));
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(new ResponseMessage(e.getMessage()));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Error updating issue priority: " + e.getMessage()));
	    }
	}
	
	@ApiOperation(value = "Get all resolved issues")
	@RequestMapping(value = "/resolvedIssues", method = RequestMethod.GET)
	public ResponseEntity<?> getResolvedIssues() {
	    try {
	        List<IssuesDto> resolvedIssues = empService.getResolvedIssues();
	        return ResponseEntity.ok(resolvedIssues);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(new ResponseMessage(e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Error fetching resolved issues: " + e.getMessage()));
	    }
	}
	
	@ApiOperation(value = "Get Resolved Issues assigned to an Employee by ID")
	@RequestMapping(value = "/resolvedIssues/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getResolvedIssuesByEmployee(@PathVariable Long id) {
	    try {
	        List<IssuesDto> resolvedIssues = empService.getResolvedIssuesByEmployeeId(id);
	        return ResponseEntity.ok(resolvedIssues);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(new ResponseMessage(e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Error fetching resolved issues: " + e.getMessage()));
	    }
	}
	
	@ApiOperation(value = "Get Employees with Pagination, Search and Filters")
	@GetMapping("/employeeList")
	public ResponseEntity<Map<String, Object>> getAllEmployees(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String designation,
	        @RequestParam(required = false) String search) {

	    try {
	        List<Employee> allEmployees = empService.getAllEmployeesFiltered(designation, search);

	        List<EmployeeDTO> allEmployeesDTO = allEmployees.stream().map(emp -> {
	            EmployeeDTO dto = new EmployeeDTO();
	            dto.setId(emp.getId());
	            dto.setUsername(emp.getUsername());
	            dto.setFullName(emp.getFullname());
	            dto.setDesignation(emp.getDesignation());
	            dto.setEmail(emp.getEmail());
	            dto.setMobileNumber(emp.getMobileNumber());
	            dto.setLocation(emp.getLocation());
	            dto.setActive(emp.isActive());
	          
	            return dto;
	        }).collect(Collectors.toList());

	        int start = page * size;
	        int end = Math.min(start + size, allEmployeesDTO.size());
	        if(start >= allEmployeesDTO.size() && allEmployeesDTO.size() > 0) {
	            start = 0;
	            end = Math.min(size, allEmployeesDTO.size());
	        }
	        List<EmployeeDTO> pagedEmployees = start < end ? allEmployeesDTO.subList(start, end) : List.of();

	        Map<String, Object> response = new HashMap<>();
	        response.put("employees", pagedEmployees);
	        response.put("currentPage", page);
	        response.put("totalItems", allEmployeesDTO.size());
	        response.put("totalPages", (int) Math.ceil((double) allEmployeesDTO.size() / size));

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Error fetching employees: " + e.getMessage()));
	    }
	}
	
	@ApiOperation(value = "Check if email exists")
    @RequestMapping(value = "/check-email", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam @Email String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists = empService.emailExists(email);
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
	            boolean result = empService.savePassword(email, password);
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
}