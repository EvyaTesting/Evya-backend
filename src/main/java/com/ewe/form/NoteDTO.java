package com.ewe.form;

import java.time.LocalDateTime;
public class NoteDTO {
    private Long employeeId;
    private Long recipientId;
    private Long taskId;
    private Long issueId;
    private String title;
    private String description;
    private String createdByRole;

    // Default constructor
    public NoteDTO() {}

    // Getters and setters
    public Long getEmployeeId() {
    	return employeeId;
    	}
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatedByRole() { return createdByRole; }
    public void setCreatedByRole(String createdByRole) { this.createdByRole = createdByRole; }
}