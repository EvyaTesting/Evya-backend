package com.ewe.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ewe.exception.UserNotFoundException;
import com.ewe.form.IssuesDto;
import com.ewe.form.IssuesDto.Note;
import com.ewe.messages.ResponseMessage;
import com.ewe.pojo.IssueReporting;
import com.ewe.service.TicketService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Scope("request")
@RequestMapping("/services/issues")
@Api(tags = "Ticket Management")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @ApiOperation(value = "Get All Tickets")
    @GetMapping("/tickets")
    public ResponseEntity<List<IssuesDto>> getAllTickets(@RequestParam(required = false) Long orgId) {
        logger.debug("Fetching all tickets");
        return ResponseEntity.ok(ticketService.getAllTickets(orgId));
    }

    @ApiOperation(value = "Get Ticket by ID")
    @GetMapping("/tickets/{id}")
    public ResponseEntity<IssuesDto> getTicketById(@PathVariable Long id) throws UserNotFoundException {
        logger.debug("Fetching ticket by ID: {}", id);
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }
    
    @ApiOperation(value = "Get Tickets by User ID")
    @GetMapping("/tickets/user/{userId}")
    public ResponseEntity<List<IssuesDto>> getTicketsByUserId(@PathVariable Long userId)
            throws UserNotFoundException {

        logger.debug("Fetching tickets for userId: {}", userId);
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId));
    }
    
    @GetMapping("/{ticketId}")
    public ResponseEntity<?> getIssueByTicketId(@PathVariable String ticketId) throws UserNotFoundException {
        IssuesDto issue = ticketService.getIssueByTicketId(ticketId);
        if (issue == null) {
            return ResponseEntity.ok("No Issues Found");
        }
        return ResponseEntity.ok(issue);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getIssuesByStatusWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String search) {

        try {
            // Get filtered issues with optional search
            List<IssueReporting> allIssues = ticketService.getIssuesByStatus(orgId, search);
            
            // Rest of your existing pagination logic
            int start = page * size;
            int end = Math.min(start + size, allIssues.size());

            if (start >= allIssues.size() && allIssues.size() > 0) {
                start = 0;
                end = Math.min(size, allIssues.size());
            }

            List<IssueReporting> pagedIssues = start < end ? allIssues.subList(start, end) : new ArrayList<>();

            Map<String, Object> response = new HashMap<>();
            response.put("content", pagedIssues);
            response.put("currentPage", page);
            response.put("totalItems", allIssues.size());
            response.put("totalPages", (int) Math.ceil((double) allIssues.size() / size));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error fetching issues");
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Create Ticket")
    @PostMapping("/createticket")
    public ResponseEntity<IssuesDto> createTicket(@Valid @RequestBody IssuesDto issuesDto)
            throws UserNotFoundException, MessagingException {
        logger.debug("Creating new ticket: {}", issuesDto);
        IssuesDto created = ticketService.addTicket(issuesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @ApiOperation(value = "Update Ticket")
    @PutMapping("UpdateTicket/{id}")
    public ResponseEntity<IssuesDto> updateTicket(@PathVariable Long id, @Valid @RequestBody IssuesDto issuesDto)
            throws UserNotFoundException, MessagingException {
        logger.debug("Updating ticket ID {}: {}", id, issuesDto);
        IssuesDto updated = ticketService.updateTicket(id, issuesDto);
        return ResponseEntity.ok(updated);
    }

    @ApiOperation(value = "Delete Ticket")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteTicket(@PathVariable Long id) throws UserNotFoundException {
        logger.debug("Deleting ticket ID: {}", id);
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(new ResponseMessage("Ticket deleted successfully"));
    }

    @ApiOperation(value = "Add Note to Ticket")
    @PostMapping("/notes/{id}")
    public ResponseEntity<IssuesDto> addNoteToTicket(
            @PathVariable Long id,
            @Valid @RequestBody Note noteDto) throws UserNotFoundException {
        logger.debug("Adding note to ticket ID {}: {}", id, noteDto);
        IssuesDto updatedTicket = ticketService.addNoteToTicket(id, noteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedTicket);
    }

    @ApiOperation(value = "Update Note")
    @PutMapping("/{id}/notes/{noteId}")
    public ResponseEntity<IssuesDto> updateNote(
            @PathVariable("id") Long id,
            @PathVariable("noteId")  Long noteId,
            @Valid @RequestBody Note noteDto) throws UserNotFoundException {
        logger.debug("Updating note ID {} in ticket ID {}: {}", noteId, id, noteDto);
        IssuesDto updatedTicket = ticketService.updateNote(id, noteId, noteDto);
        return ResponseEntity.ok(updatedTicket);
    }

    @ApiOperation(value = "Delete Note")
    @DeleteMapping("/{id}/notes/{noteId}")
    public ResponseEntity<ResponseMessage> deleteNote(
            @PathVariable Long id,
            @PathVariable Long noteId) throws UserNotFoundException {
        logger.debug("Deleting note ID {} in ticket ID {}", noteId, id);
        ticketService.deleteNote(id, noteId);
        return ResponseEntity.ok(new ResponseMessage("Note deleted successfully"));
    }
}