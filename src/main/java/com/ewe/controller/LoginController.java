package com.ewe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.ewe.controller.advice.ServerException;
import com.ewe.form.EmployeeDTO;
import com.ewe.form.LoginUser;
import com.ewe.form.UserDetailsForm;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.Employee;
import com.ewe.pojo.User;
import com.ewe.service.EmployeeService;
import com.ewe.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Scope("request")
@Hidden
@RequestMapping("login/")
@Api(tags = "LoginController")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private EmployeeService empService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @ApiOperation(value = "Unified Authentication Endpoint for Users and Employees")
    @RequestMapping(value = "authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody LoginUser loginUser) throws Exception {
    	String rawPassword = new String(java.util.Base64.getDecoder().decode(loginUser.getPassword()));
    	User user = (User) this.userService.getUser(loginUser.getUsername());
    	if (user != null) {
    		return authenticateUser(user, rawPassword);
    	}
    	Employee employee = userService.getEmployeeByUsername(loginUser.getUsername());
    	if (employee != null) {
    		return authenticateEmployee(employee, rawPassword);
    	}
    	throw new ServerException(com.ewe.messages.Error.USER_NOT_EXIST.toString(),
        Integer.toString(com.ewe.messages.Error.USER_NOT_EXIST.getCode()));
    }
    
    private ResponseEntity<User> authenticateUser(User user, String rawPassword) throws ServerException {
    User userdetails = userService.getProfileById(user.getId());
    if (userdetails.getEmail() == null) {
       throw new ServerException(com.ewe.messages.Error.USER_NOT_EXIST.toString(),
               Integer.toString(com.ewe.messages.Error.USER_NOT_EXIST.getCode()));
	   }
	 
	   String storedHashedPassword = userdetails.getPasswords().iterator().next().getPassword();
	   boolean isPasswordValid = BCrypt.checkpw(rawPassword, storedHashedPassword);
	
	   if (!isPasswordValid) {
	       throw new ServerException(com.ewe.messages.Error.AUTHENTICATION.toString(),
	               Integer.toString(com.ewe.messages.Error.AUTHENTICATION.getCode()));
	   }
	   
	   return ResponseEntity.status(HttpStatus.OK).body(user);
	}

    private ResponseEntity<EmployeeDTO> authenticateEmployee(Employee emp, String rawPassword) throws ServerException {
	Employee empDetails = empService.getProfileById(emp.getId());
	
	if(empDetails.getEmail() == null) {
		throw new ServerException(com.ewe.messages.Error.USER_NOT_EXIST.toString(),
	               Integer.toString(com.ewe.messages.Error.USER_NOT_EXIST.getCode()));
	   }
	
	 String storedHashedPassword = empDetails.getPasswords().iterator().next().getPassword();
	   boolean isPasswordValid = BCrypt.checkpw(rawPassword, storedHashedPassword);

	   if (!isPasswordValid) {
	       throw new ServerException(com.ewe.messages.Error.AUTHENTICATION.toString(),
	               Integer.toString(com.ewe.messages.Error.AUTHENTICATION.getCode()));
	   }
	   
	   EmployeeDTO dto = new EmployeeDTO();
	    dto.setId(empDetails.getId());
	    dto.setUsername(empDetails.getUsername());
	    dto.setEmail(empDetails.getEmail());
	    dto.setDesignation(empDetails.getDesignation());
	    dto.setActive(empDetails.isActive());
	    dto.setRoleId(6);
	   
	   return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

    @ApiOperation(value = "Registration For New User")
    @RequestMapping(value = "registration", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> registration(@RequestBody UserDetailsForm user) throws Exception {
        userService.addDriver(user);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Registered successfully"));
    }
}
