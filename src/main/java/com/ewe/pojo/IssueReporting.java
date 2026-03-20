package com.ewe.pojo;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
@Entity
@Table(name = "issue_reporting")
public class IssueReporting extends BaseEntity {

private static final long serialVersionUID=1L;

private Set<Notes> note = new HashSet<Notes>(0);
@OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
@JsonIgnore
public Set<Notes> getNote() {
	return note;
}

public void setNote(Set<Notes> note) {
	this.note = note;
}

private Employee employee;
@ManyToOne(fetch = FetchType.LAZY, targetEntity = Employee.class)
@JoinColumn(name = "employee_id")
@JsonIgnore
    public Employee getEmployee() {
	return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    
    private String issue;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;

    @Column(unique = true)
    private String ticketId;

    @Column(nullable = false)
    private Long userId;

    private String category;

    private String categoryId;

    private String comment;
    
    private String priority;
    
    private Long orgId;

    private String email;

    private String mobileNumber;

//    private String assignedTo;

    @JsonProperty
    private Set<IssueNotes> notes = new HashSet<IssueNotes>();
    
    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getIssue() { return issue; }

    public void setIssue(String issue) { this.issue = issue; }

    public Date getCreatedDate() { return createdDate; }

    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public String getTicketId() { return ticketId; }

    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getCategoryId() { return categoryId; }

    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Long getOrgId() { return orgId; }

    public void setOrgId(Long orgId) { this.orgId = orgId; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getMobileNumber() { return mobileNumber; }

    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

//    public String getAssignedTo() { return assignedTo; }

//    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    @OneToMany(mappedBy = "issueReporting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public Set<IssueNotes> getNotes() { return notes; }

    public void setNotes(Set<IssueNotes> notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "IssueReporting{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", issue='" + issue + '\'' +
                ", createdDate=" + createdDate +
                ", ticketId='" + ticketId + '\'' +
                ", userId=" + userId +
                ", category='" + category + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", comment='" + comment + '\'' +
                ", priority='" + priority + '\'' +
                ", orgId=" + orgId +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                '}';
    }
}