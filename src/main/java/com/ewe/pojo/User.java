package com.ewe.pojo;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name = "Users", uniqueConstraints = {@UniqueConstraint(columnNames = "mobileNumber"),@UniqueConstraint(columnNames = "email"),@UniqueConstraint(columnNames = "username")})
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class User extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
    @Size(max = 50, message = "Full name must be at most 50 characters")
    @Pattern(regexp = "^[a-zA-Z , .]+$", message = "Full name can contain only letters and spaces")
	@Column(name = "fullname", nullable = true, length = 50)
	private String fullname;
   
	@Column(name = "username",  nullable = true, length = 50,unique = true)
	private String username;
	
    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email must be at most 50 characters")
	@Column(name = "email",  nullable = true, length = 50,unique = true)
	private String email;

	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@Column(name = "mobileNumber",  nullable = false, length = 50)
	private String mobileNumber;
	
	@Column(name = "orgId")
    private Long orgId;
	
	public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    private String orgName;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

	@Valid
	private Set<Address> address = new HashSet<Address>(0);
@Valid
	private Set<Password> passwords = new HashSet<Password>(0);
@Valid
	private Set<Accounts> account = new HashSet<Accounts>(0);
@Valid
	private Set<Vehicles> vehicle = new HashSet<Vehicles>(0);
@Valid
	private Set<Usersinroles> usersinroles = new HashSet<Usersinroles>(0);
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
	public Set<Vehicles> getVehicle() {
		return vehicle;
	}

	public void setVehicle(Set<Vehicles> vehicle) {
		this.vehicle = vehicle;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
	public Set<Address> getAddress() {
		return address;
	}

	public void setAddress(Set<Address> address) {
		this.address = address;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
	public Set<Password> getPasswords() {
		return passwords;
	}

	public void setPasswords(Set<Password> passwords) {
		this.passwords = passwords;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
	public Set<Accounts> getAccount() {
		return account;
	}

	public void setAccount(Set<Accounts> account) {
		this.account = account;
	}
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
	public Set<Usersinroles> getUsersinroles() {
		return usersinroles;
	}

	public void setUsersinroles(Set<Usersinroles> usersinroles) {
		this.usersinroles = usersinroles;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setUpdatedAt(LocalDateTime now) {
		// TODO Auto-generated method stub		
	}

}