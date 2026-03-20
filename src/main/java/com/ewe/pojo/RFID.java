package com.ewe.pojo;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "rfid")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "account" })
public class RFID extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	private String rfId;
	private String phone;
	private String rfidHex;

	@Temporal(TemporalType.DATE)
	@Column(name = "expiryDate", length = 10)
	private Date expiryDate;

	private String status;
	private Accounts account;
	private long userId;
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
//	private User user;
//	@ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
//	@JoinColumn(name = "userId")
//	public User getUser() {
//		return user;
//	}
//	public void setUser(User user) {
//		this.user = user;
//	}
	
	public String getRfId() {
		return rfId;
	}

	public void setRfId(String rfId) {
		this.rfId = rfId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRfidHex() {
		return rfidHex;
	}

	public void setRfidHex(String rfidHex) {
		this.rfidHex = rfidHex;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "account_id")
	public Accounts getAccount() {
		return account;
	}

	public void setAccount(Accounts account) {
		this.account = account;
	}
}
