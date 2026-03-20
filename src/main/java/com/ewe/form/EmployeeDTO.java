package com.ewe.form;

import java.time.LocalDate;

public class EmployeeDTO {
    private Long id;
    private String designation;
    private LocalDate joiningDate;
    private String username;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String location;
    private boolean isActive;
    private Long whiteLabelOrgId;
    private String password;
    private String confirmPassword;
    private Integer roleId;

    // Constructors
    public EmployeeDTO() {}

    public EmployeeDTO(
        Long id, String designation, LocalDate joiningDate, String username,
        String mobileNumber, String email, String location, boolean isActive,
        Long whiteLabelOrgId, String whiteLabelOrgName, Integer roleId
    ) {
        this.id = id;
        this.designation = designation;
        this.joiningDate = joiningDate;
        this.username = username;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.location = location;
        this.isActive = isActive;
        this.whiteLabelOrgId = whiteLabelOrgId;
        this.roleId= roleId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }
    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getUsername() {
        return username;
    }
    
    public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setUsername(String username) {
        this.username = username;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Long getWhiteLabelOrgId() {
        return whiteLabelOrgId;
    }
    public void setWhiteLabelOrgId(Long whiteLabelOrgId) {
        this.whiteLabelOrgId = whiteLabelOrgId;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }    
}
