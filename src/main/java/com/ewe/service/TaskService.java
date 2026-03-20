package com.ewe.service;
import java.util.List;

import com.ewe.form.TaskAssignmentDTO;

import com.ewe.exception.UserNotFoundException;

import com.ewe.pojo.TaskAssignment;

public interface TaskService {

    TaskAssignment assignTask(TaskAssignmentDTO taskAssignmentDTO) throws UserNotFoundException;

    List<TaskAssignment> getTasksByEmployee(Long Id) throws UserNotFoundException;

    List<TaskAssignment> getAllTasks();

    void deleteTask(Long id) throws UserNotFoundException;

    void updateTask(Long taskId, TaskAssignmentDTO taskDTO) throws UserNotFoundException;
    
    TaskAssignment getTaskById(Long id) throws UserNotFoundException;

    long getTaskCount();

    long getTaskCountByEmployee(Long employeeId) throws UserNotFoundException;
    
    TaskAssignment updateTaskStatus(Long taskId, String status) throws UserNotFoundException;

	List<TaskAssignment> getTasksFiltered(Long employeeId, String status, String search);
	// Add this method to your TaskService interface
	List<TaskAssignment> getAllTasksFiltered(String search) throws UserNotFoundException;
	}