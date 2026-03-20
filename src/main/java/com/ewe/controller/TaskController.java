package com.ewe.controller;
	
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ewe.exception.UserNotFoundException;
import com.ewe.form.TaskAssignmentDTO;
import com.ewe.pojo.TaskAssignment;
import com.ewe.service.TaskService;

import io.swagger.annotations.ApiOperation;
	
	@RestController
	@RequestMapping("/tasks")
	public class TaskController {
	
	@Autowired
	private TaskService taskService;

	    @PostMapping("/assignTask")
	    public ResponseEntity<TaskAssignmentDTO> assignTask(@RequestBody TaskAssignmentDTO dto) throws UserNotFoundException {

	        TaskAssignment savedTask = taskService.assignTask(dto);
	        TaskAssignmentDTO response = new TaskAssignmentDTO();
	        response.setId(savedTask.getId());
	        response.setTaskName(savedTask.getTaskName());
	        response.setDescription(savedTask.getDescription());
	        response.setStatus(savedTask.getStatus());
	        response.setPriority(savedTask.getPriority());
	        response.setDueDate(savedTask.getDueDate());
	        response.setLocation(savedTask.getLocation());
	        response.setCreatedAt(savedTask.getCreatedAt());
	        response.setUpdatedAt(savedTask.getUpdatedAt());
	        response.setId(savedTask.getId());
	        return ResponseEntity.ok(response);
	    }

	@ApiOperation(value = "Get tasks by Employee ID")
	@RequestMapping( value = "/employee/{Id}/tasks", method = RequestMethod.GET)
	public ResponseEntity<List<TaskAssignment>> getTasksByEmployee(@PathVariable Long Id)
	throws UserNotFoundException {
	   return ResponseEntity.ok(taskService.getTasksByEmployee(Id));
	}
	
	@ApiOperation(value = "Get all tasks")
	    @RequestMapping(value = "/all", method = RequestMethod.GET)
	    public ResponseEntity<List<TaskAssignment>> getAllTasks() {
	        return ResponseEntity.ok(taskService.getAllTasks());
	    }
	
	@ApiOperation(value = "Delete a task by ID")
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long id) throws UserNotFoundException {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }
	
	@ApiOperation(value = "Update the task with ID")
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.PUT )
	public ResponseEntity<String> updateTask(@PathVariable("id") Long id, @RequestBody TaskAssignmentDTO taskDTO)
	            throws UserNotFoundException {
	        taskService.updateTask(id, taskDTO);
	        return ResponseEntity.ok("Task updated successfully");
	    }
	
	@ApiOperation(value = "Get task by ID")
	@RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public ResponseEntity<TaskAssignment> getTaskById(@PathVariable("id") Long id)
            throws UserNotFoundException {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

	@ApiOperation(value = "Get total task count")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public ResponseEntity<Long> getTaskCount() {
	   return ResponseEntity.ok(taskService.getTaskCount());
	}
	
	@ApiOperation(value = "Get task count by Employee ID")
	@RequestMapping(value = "/employee/{id}/count", method = RequestMethod.GET)
	public ResponseEntity<Long> getTaskCountByEmployee(@PathVariable("id") Long id) throws UserNotFoundException {
	   return ResponseEntity.ok(taskService.getTaskCountByEmployee(id));
	}
	
	@ApiOperation(value = "Update task status")
	@RequestMapping(value = "/{id}/status", method = RequestMethod.PUT)
	public ResponseEntity<?> updateTaskStatus(
	        @PathVariable("id") Long id,
	        @RequestParam String status) {
	    try {
	        TaskAssignmentDTO dto = new TaskAssignmentDTO();
	        dto.setStatus(status);

	        taskService.updateTask(id, dto); // ✅ Uses unified update method

	        return ResponseEntity.ok("Status updated successfully");
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Unexpected error: " + e.getMessage());
	    }
	}	
	
	@ApiOperation(value = "Get tasks with Pagination and Search by Employee")
	@RequestMapping(value = "/paged", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getPagedTasks(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String status,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) Long employeeId) {

	    List<TaskAssignment> allTasks = taskService.getTasksFiltered(employeeId, status, search);

	    // Manual pagination
	    int start = page * size;
	    int end = Math.min(start + size, allTasks.size());
	    List<TaskAssignment> pagedTasks = start < end ? allTasks.subList(start, end) : List.of();

	    Map<String, Object> response = new HashMap<>();
	    response.put("tasks", pagedTasks);
	    response.put("currentPage", page);
	    response.put("totalItems", allTasks.size());
	    response.put("totalPages", (int) Math.ceil((double) allTasks.size() / size));

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	 @ApiOperation(value = "Get task duration in hours")
	    @RequestMapping(value = "/{id}/duration", method = RequestMethod.GET)
	    public ResponseEntity<Map<String, Object>> getTaskDuration(@PathVariable("id") Long id) throws UserNotFoundException {
	        TaskAssignment task = taskService.getTaskById(id);
	        
	        if (task.getCreatedAt() == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Map.of("error", "Task creation time not available"));
	        }
	        
	        long hours = java.time.Duration.between(task.getCreatedAt(), LocalDateTime.now()).toHours();
	        long days = hours / 24;
	        long remainingHours = hours % 24;
	        
	        Map<String, Object> durationInfo = new HashMap<>();
	        durationInfo.put("taskId", id);
	        durationInfo.put("totalHours", hours);
	        durationInfo.put("duration", days > 0 ? days + "d " + remainingHours + "h" : hours + "h");
	        
	        durationInfo.put("createdAt", task.getCreatedAt().toString()); // This will return ISO format
	        durationInfo.put("status", task.getStatus());
	        
	        return ResponseEntity.ok(durationInfo);
	    } 
	 
	 
	 @ApiOperation(value = "Get all employees tasks with pagination and search")
	 @RequestMapping(value = "/allTasks", method = RequestMethod.GET)
	 public ResponseEntity<Map<String, Object>> getAllTasksPaged(
	         @RequestParam(defaultValue = "0") int page,
	         @RequestParam(defaultValue = "10") int size,
	         @RequestParam(required = false) String search) {

	     try {
	         List<TaskAssignment> allTasks = taskService.getAllTasksFiltered(search);

	         int start = page * size;
	         int end = Math.min(start + size, allTasks.size());
	         List<TaskAssignment> pagedTasks = start < end ? allTasks.subList(start, end) : List.of();

	         List<Map<String, Object>> taskResponse = pagedTasks.stream().map(task -> {
	             Map<String, Object> taskMap = new HashMap<>();
	             taskMap.put("id", task.getId());
	             taskMap.put("taskName", task.getTaskName());
	             taskMap.put("description", task.getDescription());
	             taskMap.put("location", task.getLocation());
	             taskMap.put("priority", task.getPriority());
	             taskMap.put("status", task.getStatus());
	             taskMap.put("dueDate", task.getDueDate());
	             taskMap.put("createdAt", task.getCreatedAt());
	             
	             if (task.getEmployee() != null) {
	                 taskMap.put("employeeId", task.getEmployee().getId());
	                 taskMap.put("employeeName", task.getEmployee().getUsername());
	                 taskMap.put("employeeEmail", task.getEmployee().getEmail());
	                 taskMap.put("employeeMobile", task.getEmployee().getMobileNumber());
	             } else {
	                 taskMap.put("employeeId", null);
	                 taskMap.put("employeeName", "Unassigned");
	                 taskMap.put("employeeEmail", null);
	                 taskMap.put("employeeMobile", null);
	             }
	             
	             return taskMap;
	         }).collect(Collectors.toList());

	         Map<String, Object> response = new HashMap<>();
	         response.put("content", taskResponse);
	         response.put("currentPage", page);
	         response.put("totalItems", allTasks.size());
	         response.put("totalPages", (int) Math.ceil((double) allTasks.size() / size));
	         response.put("size", size);

	         return new ResponseEntity<>(response, HttpStatus.OK);
	         
	     } catch (Exception e) {
	         Map<String, Object> errorResponse = new HashMap<>();
	         errorResponse.put("error", "Failed to fetch tasks: " + e.getMessage());
	         return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	 }	
	}