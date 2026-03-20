package com.ewe.service;

import java.util.List;

import com.ewe.exception.DuplicateUserException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.EmployeeDTO;
import com.ewe.form.IssuesDto;
import com.ewe.pojo.Employee;
import com.ewe.pojo.IssueReporting;
import com.ewe.pojo.User;

public interface EmployeeService {		

		void addEmployee(EmployeeDTO dto) throws DuplicateUserException, UserNotFoundException;

	    void updateEmployee(Long id, EmployeeDTO employeeDTO) throws UserNotFoundException;
	    
	    void deleteEmployee(Long id) throws UserNotFoundException;
	    
	    void deleteAllEmployees() throws UserNotFoundException;
	    
	    List<Employee> getEmployeesByDesignation(String designation) throws UserNotFoundException;
	    
	    Employee getEmployeeById(Long id) throws UserNotFoundException;

		List<Employee> getAllEmployees();
		
		List<IssueReporting> getIssuesByEmployeeId(Long employeeId) throws UserNotFoundException;

		void updateIssueStatus(Long issueId, String status) throws UserNotFoundException;
		
		List<IssuesDto> getResolvedIssues() throws UserNotFoundException;

		List<IssuesDto> getResolvedIssuesByEmployeeId(Long employeeId) throws UserNotFoundException;

		List<Employee> getAllEmployeesFiltered(String designation, String search);

		void updateIssuePriority(Long issueId, String priority) throws UserNotFoundException;

		boolean emailExists(String email);

		boolean savePassword(String email, String password) throws UserNotFoundException;

		Employee getEmpByEmail(String email) throws UserNotFoundException;

		Employee getProfileById(Long id);

}