package com.ewe.form;

import java.sql.Date;
import java.util.List;

import com.ewe.pojo.IssueReporting;

public class IssuesDto {
	 // Fields from IssueReporting
    private long id;
    private String type;
    private String status;
    private String issue;
    private Date createdDate;
    private String ticketId;
    private Long userId;
    private String category;
    private String categoryId;
    private String comment;
    private String priority;
    private Long orgId;
    private String email;
    private String mobileNumber;
    private String assignedTo;
    private Long employeeId;

	// Flattened fields from IssueNotes (for each note)
    public static class Note {
    	private long id;
        private String notes;
        private String title;
        private Date modifiedDate;
        private String createdBy;
        private String lastModifiedBy;
        private IssueReporting issueReporting;
        
        // Getters and Setters
        public String getNotes() {
            return notes;
        }
        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public Date getModifiedDate() {
            return modifiedDate;
        }
        public void setModifiedDate(Date modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public String getCreatedBy() {
            return createdBy;
        }
        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getLastModifiedBy() {
            return lastModifiedBy;
        }
        public void setLastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
        }
		public IssueReporting getIssueReporting() {
			return issueReporting;
		}
		public void setIssueReporting(IssueReporting issueReporting) {
			this.issueReporting = issueReporting;
		}
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}	
    }

    private List<Note> notes; // List of notes for this issue

    // Getters and Setters for IssueReporting fields
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
}