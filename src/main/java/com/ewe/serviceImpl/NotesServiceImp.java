package com.ewe.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ewe.dao.GeneralDao;
import com.ewe.exception.UserNotFoundException;
import com.ewe.pojo.Employee;
import com.ewe.pojo.IssueReporting;
import com.ewe.pojo.Notes;
import com.ewe.pojo.TaskAssignment;
import com.ewe.service.NotesService;

@Service
@Transactional
public class NotesServiceImp implements NotesService {

	@Autowired
	private GeneralDao<?, ?> generalDao;
	@Autowired
    private GeneralDao<Notes, Long> notesDao;
    @Autowired
    private GeneralDao<Employee, Long> employeeDao;
    @Autowired
    private GeneralDao<TaskAssignment, Long> taskDao;
    @Override
    public Notes addNote(Long employeeId, Long recipientId, Long taskId, Long issueId,
                         String title, String description, String createdByRole) throws UserNotFoundException {
        if (taskId == null && issueId == null) {
            throw new IllegalArgumentException("Either taskId or issueId must be provided");
        }
        Notes note = new Notes();
        Employee employee = (Employee) generalDao.findOneById(new Employee(), employeeId);
        note.setEmployee(employee);
        if (recipientId != null) {
            Employee recipient = (Employee) generalDao.findOneById(new Employee(), recipientId);
            note.setRecipient(recipient);
        }
        if (taskId != null) {
            TaskAssignment task = (TaskAssignment) generalDao.findOneById(new TaskAssignment(), taskId);
            note.setTask(task);
        }
        if (issueId != null) {
            IssueReporting issue = (IssueReporting) generalDao.findOneById(new IssueReporting(), issueId);
            note.setIssue(issue);
        }
        note.setTitle(title);
        note.setDescription(description);
        note.setCreatedByRole(createdByRole.toUpperCase());
        note.setCreatedDate(LocalDateTime.now());
        return (Notes) generalDao.save(note);
    }
    
    @Override
    public Notes updateNoteById(Long noteId, String title, String description) {
        try {
            Notes note = notesDao.findOneById(new Notes(), noteId);
            note.setTitle(title);
            note.setDescription(description);
            return notesDao.update(note);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update note: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteNoteById(Long noteId) {
        try {
            Notes note = notesDao.findOneById(new Notes(), noteId);
            notesDao.delete(note);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete note: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Notes> getNotesByTaskId(Long taskId) throws UserNotFoundException {
        String hql = "FROM Notes n WHERE n.task.id = ?1 ORDER BY n.createdDate DESC";
        return notesDao.findAllHQLQry(new Notes(), hql, taskId);
    }

    @Override
    public Notes getNoteById(Long noteId) throws UserNotFoundException {
        return notesDao.findOneById(new Notes(), noteId);
    }

    @Override
    public List<Notes> getNotesByIssueId(Long issueId) throws UserNotFoundException {
        String hql = "FROM Notes n WHERE n.issue.id = ?1 ORDER BY n.createdDate DESC";
        return generalDao.findAllHQLQry(new Notes(), hql, issueId);
    }


}