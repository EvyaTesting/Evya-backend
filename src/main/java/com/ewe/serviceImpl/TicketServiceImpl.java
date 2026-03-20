package com.ewe.serviceImpl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ewe.dao.GeneralDao;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.IssuesDto;
import com.ewe.pojo.Employee;
import com.ewe.pojo.IssueNotes;
import com.ewe.pojo.IssueReporting;
import com.ewe.service.EmailService;
import com.ewe.service.TicketService;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    @Autowired
    private GeneralDao<?, ?> generalDao;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private EmailService emailService;
    
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Override
    public List<IssuesDto> getAllTickets(Long orgId) {
        try {
            logger.debug("Fetching tickets for organization: {}", orgId);            
            StringBuilder hql = new StringBuilder("FROM IssueReporting");            
            if (orgId != null) {
                hql.append(" WHERE orgId = :orgId");
            }            
            hql.append(" ORDER BY createdDate DESC");
            Query query = entityManager.createQuery(hql.toString());
            if (orgId != null) {
                query.setParameter("orgId", orgId);
            }            
            List<IssueReporting> tickets = query.getResultList();
            return tickets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error fetching all tickets for organization: {}", orgId, e);
            return Collections.emptyList();
        }
    }
    @Override
    @Transactional(readOnly = true)
    public IssuesDto getTicketById(Long id) throws UserNotFoundException {
        logger.debug("Fetching ticket by ID: {}", id);
        IssueReporting ticket = generalDao.findOneById(new IssueReporting(), id);
        if (ticket == null) {
            throw new UserNotFoundException("No Issues Found");
        }
        return convertToDto(ticket);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IssuesDto> getTicketsByUserId(Long userId) throws UserNotFoundException {
        logger.debug("Fetching tickets by userId: {}", userId);

        String hql = "FROM IssueReporting i WHERE i.userId = :userId";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        List<IssueReporting> tickets = generalDao.findByHQL(hql, params);

        if (tickets == null || tickets.isEmpty()) {
            throw new UserNotFoundException("No Issues Found for userId: " + userId);
        }

        return tickets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public IssuesDto getIssueByTicketId(String ticketId) {
        try {
            String sql = "SELECT * FROM issue_reporting WHERE ticketId = '" + ticketId + "'";
            IssueReporting issue = generalDao.findOneSQLQuery(new IssueReporting(), sql);
           
            if (issue==null)
            {
                throw new UserNotFoundException("No issue found with ticketId: " + ticketId);
            }
            return convertToDto(issue);
        } catch (UserNotFoundException e) {
            logger.warn("Ticket not found with ticketId: {}", ticketId);
            return null;
        }  
    }

    @Override
    public List<IssueReporting> getIssuesByStatus(Long orgId) {
        return getIssuesByStatus(orgId, null); // Maintain backward compatibility
    }

    @Override
    public List<IssueReporting> getIssuesByStatus(Long orgId, String search) {
        try {
            StringBuilder hql = new StringBuilder("FROM IssueReporting WHERE status IN ('Open', 'In Progress')");
            
            // Add organization filter if provided
            if (orgId != null) {
                hql.append(" AND orgId = :orgId");
            }
            
            // Add search condition if provided
            if (search != null && !search.trim().isEmpty()) {
                String searchTerm = "%" + search.toLowerCase() + "%";
                hql.append(" AND (LOWER(type) LIKE :searchTerm OR " +
                          "LOWER(status) LIKE :searchTerm OR " +
                          "LOWER(issue) LIKE :searchTerm OR " +
                          "LOWER(ticketId) LIKE :searchTerm OR " +
                          "LOWER(category) LIKE :searchTerm OR " +
                          "LOWER(comment) LIKE :searchTerm OR " +
                          "LOWER(email) LIKE :searchTerm OR " +
                          "LOWER(mobileNumber) LIKE :searchTerm OR " +
                          "LOWER(assignedTo) LIKE :searchTerm)");
            }
            
            hql.append(" ORDER BY createdDate DESC");
            
            Query query = entityManager.createQuery(hql.toString());
            
            // Set parameters
            if (orgId != null) {
                query.setParameter("orgId", orgId);
            }
            
            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("searchTerm", "%" + search.toLowerCase() + "%");
            }
            
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error fetching issues by status", e);
            return Collections.emptyList();
        }
    }
    
//    @Override
//    public IssuesDto addTicket(IssuesDto issuesDto) throws UserNotFoundException, MessagingException {
//        logger.info("Creating new ticket/alert");
//        
//        IssueReporting issueReporting = convertToEntity(issuesDto);
//        validateTicket(issueReporting);
//        issueReporting.setCreatedDate(new Date());
//        issueReporting.setTicketId(generateReferenceNumber(issueReporting));
//
//        generalDao.save(issueReporting);
//        
//        if (issueReporting.getEmail() != null && !issueReporting.getEmail().isEmpty()) {
//            emailService.sendTicketCreationEmail(
//                issueReporting.getEmail(),
//                issueReporting.getEmail().split("@")[0], // Or fetch actual name from user service
//                issueReporting.getTicketId(),
//                issueReporting.getIssue(),
//                issueReporting.getCategory(),
//                issueReporting.getCreatedDate()
//            );
//        } else {
//            logger.warn("No email address provided for ticket creator. Email notification skipped.");
//        }
//        return convertToDto(issueReporting);
//    }
    
    @Override
    public IssuesDto addTicket(IssuesDto issuesDto) throws UserNotFoundException, MessagingException {
        logger.info("Creating new ticket/alert");

        IssueReporting issueReporting = convertToEntity(issuesDto);
        
        validateTicket(issueReporting);

        // Assign employee if provided
        if (issuesDto.getEmployeeId() != null) {
            Employee emp = generalDao.findOneById(new Employee(), issuesDto.getEmployeeId());
            if (emp == null) throw new UserNotFoundException("Employee not found with ID: " + issuesDto.getEmployeeId());
            issueReporting.setEmployee(emp);
        }

        issueReporting.setCreatedDate(new Date());
        issueReporting.setTicketId(generateReferenceNumber(issueReporting));
        issueReporting.setStatus(
        	    issuesDto.getStatus() != null ? issuesDto.getStatus() : "Open"
        	);

        generalDao.save(issueReporting);

        if (issueReporting.getEmail() != null && !issueReporting.getEmail().isEmpty()) {
            emailService.sendTicketCreationEmail(
                issueReporting.getEmail(),
                issueReporting.getEmail().split("@")[0],
                issueReporting.getTicketId(),
                issueReporting.getIssue(),
                issueReporting.getCategory(),
                issueReporting.getCreatedDate()
            );
        } else {
            logger.warn("No email address provided for ticket creator. Email notification skipped.");
        }

        return convertToDto(issueReporting);
    }

//    @Override
//    public IssuesDto updateTicket(Long id, IssuesDto issuesDto) 
//            throws UserNotFoundException, MessagingException {
//        
//        logger.info("Updating ticket ID: {}", id);
//
//        IssueReporting existingTicket = generalDao.findOneById(new IssueReporting(), id);
//        if (existingTicket == null) {
//            throw new UserNotFoundException("Ticket not found with ID: " + id);
//        }
//        
//        updateEntityFromDto(existingTicket, issuesDto);
//        generalDao.update(existingTicket);
//        sendStatusUpdateNotification(existingTicket);
//
//        return convertToDto(existingTicket);
//    }
    
    @Override
    public IssuesDto updateTicket(Long id, IssuesDto issuesDto) 
            throws UserNotFoundException, MessagingException {

        logger.info("Updating ticket ID: {}", id);

        IssueReporting existingTicket = generalDao.findOneById(new IssueReporting(), id);
        if (existingTicket == null) {
            throw new UserNotFoundException("Ticket not found with ID: " + id);
        }
        updateEntityFromDto(existingTicket, issuesDto);
        if (issuesDto.getEmployeeId() != null) {
            Employee emp = generalDao.findOneById(new Employee(), issuesDto.getEmployeeId());
            if (emp == null) throw new UserNotFoundException("Employee not found");
            existingTicket.setEmployee(emp);
        }
        generalDao.update(existingTicket);
        sendStatusUpdateNotification(existingTicket);
        return convertToDto(existingTicket);
    }

    @Override
    public void deleteTicket(Long id) throws UserNotFoundException {
        logger.info("Deleting ticket ID: {}", id);
        IssueReporting ticket = generalDao.findOneById(new IssueReporting(), id);
        if (ticket == null) {
            throw new UserNotFoundException("Ticket not found with ID: " + id);
        }
        generalDao.delete(ticket);
    }

    @Override
    public IssuesDto addNoteToTicket(Long id, IssuesDto.Note noteDto) 
            throws UserNotFoundException {
        logger.info("Adding note to ticket ID: {}", id);
        IssueReporting ticket = generalDao.findOneById(new IssueReporting(), id);
        if (ticket == null) {
            throw new UserNotFoundException("Ticket not found with ID: " + id);
        } 
        
        IssueNotes issueNote = new IssueNotes();
        issueNote.setNotes(noteDto.getNotes());
        issueNote.setTitle(noteDto.getTitle());
        issueNote.setIssueReporting(ticket);
        issueNote.setCreatedBy(noteDto.getCreatedBy());
        issueNote.setModifiedDate(new Date());
        
        generalDao.save(issueNote);
        ticket.getNotes().add(issueNote);
        generalDao.update(ticket);
        
        return convertToDto(ticket);
    }

    @Override
    public IssuesDto updateNote(Long id, Long noteId, IssuesDto.Note noteDto) 
            throws UserNotFoundException {
        logger.info("Updating note ID: {} for ticket ID: {}", noteId, id);
        
        IssueNotes existingNote = generalDao.findOneById(new IssueNotes(), noteId);
        if (existingNote == null) {
            throw new UserNotFoundException("Note not found with ID: " + noteId);
        }
        
//        existingNote.setNotes(noteDto.getNotes());
//        existingNote.setTitle(noteDto.getTitle());
        if (noteDto.getNotes() != null) {
            existingNote.setNotes(noteDto.getNotes());
        }

        if (noteDto.getTitle() != null) {
            existingNote.setTitle(noteDto.getTitle());
        }
        existingNote.setLastModifiedBy(noteDto.getLastModifiedBy());
        existingNote.setModifiedDate(new Date());
        
        generalDao.update(existingNote);
        IssueReporting ticket = generalDao.findOneById(new IssueReporting(), id);
        return convertToDto(ticket);
    }

//    @Override
//    public void deleteNote(Long id, Long noteId) throws UserNotFoundException {
//        logger.info("Deleting note ID: {} from ticket ID: {}", noteId, id);
//        
//        IssueNotes note = generalDao.findOneById(new IssueNotes(), noteId);
//        if (note == null) {
//            throw new UserNotFoundException("Note not found with ID: " + noteId);
//        }
//        generalDao.delete(note);
//    }
    
//    @Override
//    public void deleteNote(Long issueId, Long noteId) throws UserNotFoundException {
//
//        logger.info("Deleting note ID: {} from ticket ID: {}", noteId, issueId);
//        IssueNotes note = generalDao.findOneById(new IssueNotes(), noteId);
//        if (note == null) {
//            throw new UserNotFoundException("Note not found with ID: " + noteId);
//        }
//        IssueReporting issue = note.getIssueReporting();
//        if (issue == null || !issue.getId().equals(issueId)) {
//            throw new UserNotFoundException(
//                "Note ID " + noteId + " does not belong to Issue ID " + issueId
//            );
//        }
//        generalDao.delete(note);
//    }

    @Override
    public void deleteNote(Long issueId, Long noteId) throws UserNotFoundException {
        logger.info("Deleting note ID: {} from ticket ID: {}", noteId, issueId);
        
        // First check if the note exists and belongs to the correct issue
        IssueNotes note = generalDao.findOneById(new IssueNotes(), noteId);
        if (note == null) {
            throw new UserNotFoundException("Note not found with ID: " + noteId);
        }
        
        // Verify the note belongs to the specified issue
        IssueReporting issue = note.getIssueReporting();
        if (issue == null) {
            throw new UserNotFoundException("Note is not associated with any issue");
        }
        
        if (!issue.getId().equals(issueId)) {
            throw new UserNotFoundException(
                "Note ID " + noteId + " does not belong to Issue ID " + issueId
            );
        }
        
        // Remove the note from the issue's collection to maintain bidirectional consistency
        if (issue.getNotes() != null) {
            issue.getNotes().remove(note);
        }
        
        // Delete the note
        try {
            generalDao.delete(note);
            logger.info("Successfully deleted note ID: {}", noteId);
        } catch (Exception e) {
            logger.error("Failed to delete note ID: {}", noteId, e);
            throw new RuntimeException("Failed to delete note: " + e.getMessage(), e);
        }
    }
    private IssuesDto convertToDto(IssueReporting issueReporting) {
        IssuesDto dto = new IssuesDto();
        dto.setId(issueReporting.getId());
        dto.setType(issueReporting.getType());
        dto.setStatus(issueReporting.getStatus());
        dto.setIssue(issueReporting.getIssue());
        dto.setPriority(issueReporting.getPriority());
        
        if (issueReporting.getCreatedDate() != null) {
          //  dto.setCreatedDate(issueReporting.getCreatedDate().getTime());
        }
        dto.setTicketId(issueReporting.getTicketId());
        dto.setUserId(issueReporting.getUserId());
        dto.setCategory(issueReporting.getCategory());
        dto.setCategoryId(issueReporting.getCategoryId());
        dto.setComment(issueReporting.getComment());
        dto.setOrgId(issueReporting.getOrgId());
        dto.setEmail(issueReporting.getEmail());
        dto.setMobileNumber(issueReporting.getMobileNumber());
//        dto.setAssignedTo(issueReporting.getAssignedTo());
        
     // Set employee info if assigned
        if (issueReporting.getEmployee() != null) {
            dto.setEmployeeId(issueReporting.getEmployee().getId());
        }
        
        // Convert notes if they exist
        if (issueReporting.getNotes() != null && !issueReporting.getNotes().isEmpty()) {
            dto.setNotes(issueReporting.getNotes().stream()
                .map(note -> {
                    IssuesDto.Note noteDto = new IssuesDto.Note();
                    noteDto.setId(note.getId());
                    noteDto.setTitle(note.getTitle());
                    noteDto.setNotes(note.getNotes());
                    noteDto.setCreatedBy(note.getCreatedBy());
                    noteDto.setLastModifiedBy(note.getLastModifiedBy());
                    if (note.getModifiedDate() != null) {
                      //  noteDto.setModifiedDate(note.getModifiedDate().getTime());
                    }
                    return noteDto;
                })
                .collect(Collectors.toList()));
        }
        
        return dto;
    }    
    private IssueReporting convertToEntity(IssuesDto dto) {
        IssueReporting entity = new IssueReporting();
        entity.setType(dto.getType());
        entity.setStatus(dto.getStatus());
        entity.setPriority(dto.getPriority());
        entity.setIssue(dto.getIssue());
        entity.setUserId(dto.getUserId());
        entity.setCategory(dto.getCategory());
        entity.setCategoryId(dto.getCategoryId());
        entity.setComment(dto.getComment());
        entity.setOrgId(dto.getOrgId());
        entity.setEmail(dto.getEmail());
        entity.setMobileNumber(dto.getMobileNumber());
//        entity.setAssignedTo(dto.getAssignedTo());
        return entity;
    }
    
    private void updateEntityFromDto(IssueReporting entity, IssuesDto dto) {
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getIssue() != null) {
            entity.setIssue(dto.getIssue());
        }
        if (dto.getCategory() != null) {
            entity.setCategory(dto.getCategory());
        }
        if (dto.getCategoryId() != null) {
            entity.setCategoryId(dto.getCategoryId());
        }
        if (dto.getComment() != null) {
            entity.setComment(dto.getComment());
        }
        if (dto.getPriority() != null) {
            entity.setPriority(dto.getPriority());
        }
    }
    
    private void validateTicket(IssueReporting issueReporting) {
        if (issueReporting.getType() == null || 
           (!issueReporting.getType().equals("Ticket") && !issueReporting.getType().equals("Alert"))) {
            throw new IllegalArgumentException("Type must be either 'Ticket' or 'Alert'");
        }
        if (issueReporting.getIssue() == null || issueReporting.getIssue().isEmpty()) {
            throw new IllegalArgumentException("Issue description cannot be empty");
        }
        if (issueReporting.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
    
    private String generateReferenceNumber(IssueReporting issueReporting) {
        String prefix = issueReporting.getType().equals("Alert") ? "ALT" : "TKT";
        return prefix + "-" + System.currentTimeMillis();
    }
    
    private void sendStatusUpdateNotification(IssueReporting issueReporting) throws MessagingException {
        if ("Closed".equalsIgnoreCase(issueReporting.getStatus())) {
            try {
                emailService.sendTicketStatusUpdateEmail(
                    issueReporting.getEmail(),
                    "System",
                    issueReporting.getStatus(),
                    issueReporting.getTicketId(),
                    issueReporting.getComment()
                );
                logger.info("Status update email sent for ticket {}", issueReporting.getTicketId());
            } catch (MessagingException e) {
                logger.error("Failed to send status email for ticket {}", issueReporting.getTicketId(), e);
                throw e;
            }
        }
    }
}