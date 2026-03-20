package com.ewe.pojo;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "notes")
public class Notes extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private LocalDateTime createdDate;
    private String createdByRole;
    
    private TaskAssignment task;
    private Employee employee;
    private Employee recipient;
    private IssueReporting issue;
    
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = IssueReporting.class)
	@JoinColumn(name = "issue_id", nullable=true)
    @JsonIgnore
	public IssueReporting getIssue() {
		return issue;
	}

	public void setIssue(IssueReporting issue) {
		this.issue = issue;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", referencedColumnName = "id", nullable = true)
	@JsonIgnore
	public TaskAssignment getTask() {
		 return task;
	}

	public void setTask(TaskAssignment task) {
	     this.task = task;
	}
	 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	public Employee getEmployee() {
	    return employee;
	}

	public void setEmployee(Employee employee) {
	    this.employee = employee;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipient_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	public Employee getRecipient() {
	     return recipient;
	}

	public void setRecipient(Employee recipient) {
	    this.recipient = recipient;
	}

	@Column(name = "created_by_role", nullable = false)
    public String getCreatedByRole() {
		return createdByRole;
	}

	public void setCreatedByRole(String createdByRole) {
		this.createdByRole = createdByRole;
	}

	@Column(name = "title", nullable = false, length = 200)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "created_date")
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}