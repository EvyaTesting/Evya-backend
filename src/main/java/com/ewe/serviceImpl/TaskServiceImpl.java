package com.ewe.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ewe.dao.GeneralDao;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.TaskAssignmentDTO;
import com.ewe.pojo.Employee;
import com.ewe.pojo.TaskAssignment;
import com.ewe.service.TaskService;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private GeneralDao<?, ?> generalDao;

    @Override
    public TaskAssignment assignTask(TaskAssignmentDTO dto) throws UserNotFoundException {
    Employee employee = (Employee) generalDao.findOneById(new Employee(), dto.getEmployeeId());
    if (employee == null) {
       throw new UserNotFoundException("Employee not found with id: " + dto.getEmployeeId());
    }
    TaskAssignment taskAssignment = new TaskAssignment();
    taskAssignment.setTaskName(dto.getTaskName());
    taskAssignment.setDescription(dto.getDescription());
    taskAssignment.setEmployee(employee);
    taskAssignment.setLocation(dto.getLocation());
    taskAssignment.setPriority(dto.getPriority());
    taskAssignment.setDueDate(dto.getDueDate());
    taskAssignment.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
    
    taskAssignment.setCreatedAt(LocalDateTime.now());
    taskAssignment.setUpdatedAt(LocalDateTime.now());
    
    generalDao.save(taskAssignment);
    return taskAssignment;
    }
    
    @Override
    public List<TaskAssignment> getTasksByEmployee(Long employeeId) throws UserNotFoundException {
        Employee employee = (Employee) generalDao.findOneById(new Employee(), employeeId);

        if (employee == null) {
            throw new UserNotFoundException("Employee not found with id: " + employeeId);
        }
        String hql = "FROM TaskAssignment t WHERE t.employee.id = " + employeeId;
        return generalDao.findAllHQLQuery(new TaskAssignment(), hql);
    }

    @Override
    public void deleteTask(Long id) throws UserNotFoundException {
        TaskAssignment task = (TaskAssignment) generalDao.findOneById(new TaskAssignment(), id);
        if (task == null) {
            throw new UserNotFoundException("Task not found with id: " + id);
        }
        generalDao.delete(task);
    }

    @Override
    public List<TaskAssignment> getAllTasks() {
        return generalDao.findAll(new TaskAssignment());
    }
   
    @Override
    public void updateTask(Long taskId, TaskAssignmentDTO dto) throws UserNotFoundException {
        TaskAssignment task = (TaskAssignment) generalDao.findOneById(new TaskAssignment(), taskId);
        if (task == null) {
            throw new UserNotFoundException("Task not found with id: " + taskId);
        }
        if (dto.getEmployeeId() != null &&
            (task.getEmployee() == null || !dto.getEmployeeId().equals(task.getEmployee().getId()))) {
            Employee employee = (Employee) generalDao.findOneById(new Employee(), dto.getEmployeeId());
            if (employee == null) {
                throw new UserNotFoundException("Employee not found with id: " + dto.getEmployeeId());
            }
            task.setEmployee(employee);
        }
        if (dto.getTaskName() != null) {
            task.setTaskName(dto.getTaskName());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        // ✅ Update location
        if (dto.getLocation() != null) {
            task.setLocation(dto.getLocation());
        }
        if (dto.getPriority() != null) {
            task.setPriority(dto.getPriority());
        }
        if (dto.getDueDate() != null) {
            task.setDueDate(dto.getDueDate());
        }
        if (dto.getStatus() != null) {
            String normalizedStatus = dto.getStatus().trim().toUpperCase();
            if (!normalizedStatus.equals("PENDING") &&
                !normalizedStatus.equals("INPROGRESS") &&
                !normalizedStatus.equals("COMPLETED")) {
                throw new IllegalArgumentException("Invalid status: " + dto.getStatus());
            }
            task.setStatus(normalizedStatus);
        }
        generalDao.update(task);
    }
   
    @Override
    public TaskAssignment getTaskById(Long id) throws UserNotFoundException {
        TaskAssignment task = (TaskAssignment) generalDao.findOneById(new TaskAssignment(), id);
        if (task == null) {
            throw new UserNotFoundException("Task not found with id: " + id);
        }
        return task;
    }
    
    @Override
    public long getTaskCount() {
        return generalDao.findAll(new TaskAssignment()).size(); 
    }
   
    @Override
    public long getTaskCountByEmployee(Long employeeId) throws UserNotFoundException {
        List<TaskAssignment> tasks = getTasksByEmployee(employeeId);
        return tasks.size();
    }    
    
    @Override
    public TaskAssignment updateTaskStatus(Long taskId, String status) throws UserNotFoundException {
        TaskAssignment task = (TaskAssignment) generalDao.findOneById(new TaskAssignment(), taskId);
        if (task == null) {
            throw new UserNotFoundException("Task not found with id: " + taskId);
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Valid statuses are: PENDING, IN_PROGRESS, COMPLETED");
        }
        task.setStatus(status);
        generalDao.update(task);
        
        return task;
    }
    
    private boolean isValidStatus(String status) {
        return status != null && 
               (status.equals("PENDING") || 
                status.equals("IN_PROGRESS") || 
                status.equals("COMPLETED"));
    }	
    
    @Override
    public List<TaskAssignment> getAllTasksFiltered(String search) throws UserNotFoundException {
        if (search == null || search.trim().isEmpty()) {
            String hql = "FROM TaskAssignment t LEFT JOIN FETCH t.employee ORDER BY t.createdAt DESC";
            return generalDao.findAllHQLQuery(new TaskAssignment(), hql);
        }
        String hql = "FROM TaskAssignment t LEFT JOIN FETCH t.employee WHERE " +
                     "LOWER(t.taskName) LIKE :search OR " +
                     "LOWER(t.description) LIKE :search OR " +
                     "LOWER(t.location) LIKE :search OR " +
                     "LOWER(t.employee.username) LIKE :search OR " +
                     "LOWER(t.employee.email) LIKE :search OR " +
                     "t.employee.mobileNumber LIKE :search " +
                     "ORDER BY t.createdAt DESC";
        
        Map<String, Object> params = new HashMap<>();
        params.put("search", "%" + search.toLowerCase() + "%");
        
        return generalDao.findByHQL(hql, params);
    }

    
    @Override
    public List<TaskAssignment> getTasksFiltered(Long employeeId, String status, String search) {
        StringBuilder hql = new StringBuilder("FROM TaskAssignment t LEFT JOIN FETCH t.employee WHERE 1=1");
        Map<String, Object> params = new HashMap<>();
        if (employeeId != null) {
            hql.append(" AND t.employee.id = :employeeId");
            params.put("employeeId", employeeId);
        }
        if (status != null && !status.trim().isEmpty()) {
            hql.append(" AND t.status = :status");
            params.put("status", status.toUpperCase());
        }
        if (search != null && !search.trim().isEmpty()) {
            hql.append(" AND (LOWER(t.taskName) LIKE :search OR " +
                       "LOWER(t.description) LIKE :search OR " +
                       "LOWER(t.location) LIKE :search OR " +
                       "LOWER(t.employee.username) LIKE :search OR " +
                       "LOWER(t.employee.email) LIKE :search OR " +
                       "t.employee.mobileNumber LIKE :search)");
            params.put("search", "%" + search.toLowerCase() + "%");
        }
        hql.append(" ORDER BY t.createdAt DESC");
        return generalDao.findByHQL(hql.toString(), params);
    }    
}