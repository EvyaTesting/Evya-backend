package com.ewe.form;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TaskAssignmentDTO {
	
    private Long id; // Add this field


    public Long getId() {
		return id;
	}

	private String taskName;

    private String description;

    private Long employeeId;
    
    private String location;
    
    private String status;
    
    private String priority;
    
    private String dueDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt; 
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt; 
    
    public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

    public String getTaskName() {

        return taskName;

    }

    public void setTaskName(String taskName) {

        this.taskName = taskName;

    }

    public String getDescription() {

        return description;

    }

    public void setDescription(String description) {

        this.description = description;

    }

    public Long getEmployeeId() {

        return employeeId;

    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;

    }

	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}

}