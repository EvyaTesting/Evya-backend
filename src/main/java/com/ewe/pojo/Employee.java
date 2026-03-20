package com.ewe.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "employee")
public class Employee extends BaseEntity {

    private static final long serialVersionUID = 1L;
  
    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "joining_date")
    private LocalDate joiningDate;
  
    @Column(name="user_name")
    private String username;
    
    @Column (name = "full_name")
    private String fullname;
   
    @Column(name = "mobile_number")
    private String mobileNumber;
   
    @Column(name= "email")
    private String email;

    @Column(name = "location")
    private String location;

    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Valid
    @JsonIgnore
	private Set<Password> passwords = new HashSet<Password>(0);
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "employee")
	public Set<Password> getPasswords() {
		return passwords;
	}
	public void setPasswords(Set<Password> passwords) {
		this.passwords = passwords;
	}

    private Set<IssueReporting>issues=new HashSet<IssueReporting>(0);
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
    public Set<IssueReporting> getIssues() {
		return issues;
    }
	public void setIssues(Set<IssueReporting> issues) {
		this.issues = issues;
	}
	
	private Set<Notes> notes = new HashSet<Notes>(0);
    private Set<TaskAssignment> task=new HashSet<TaskAssignment>(0);
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	public Set<TaskAssignment> getTask() {
	return task;
	}
   
    public void setTask(Set<TaskAssignment> task) {
	this.task = task;
	}

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	public Set<Notes> getNotes() {
		return notes;
	}
	public void setNotes(Set<Notes> notes) {
		this.notes = notes;
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
	public void setUsername(String username) {
	this.username = username;
	}
	
	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
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

	public void setUpdatedAt(LocalDateTime now) {		
	}
}