package com.ewe.service;

import java.util.List;

import javax.mail.MessagingException;

import com.ewe.controller.advice.ServerException;
import com.ewe.exception.UserNotFoundException;
import com.ewe.form.IssuesDto;
import com.ewe.form.IssuesDto.Note;
import com.ewe.messages.PagedResult;
import com.ewe.pojo.IssueNotes;
import com.ewe.pojo.IssueReporting;

public interface TicketService {

	List<IssuesDto> getAllTickets(Long orgId);

	IssuesDto getTicketById(Long id) throws UserNotFoundException;
	
	IssuesDto getIssueByTicketId(String ticketId) throws UserNotFoundException;
   

	IssuesDto addTicket(IssuesDto issuesDto) throws UserNotFoundException, MessagingException;

	IssuesDto updateTicket(Long id, IssuesDto issuesDto) throws UserNotFoundException, MessagingException;

	void deleteTicket(Long id) throws UserNotFoundException;

	IssuesDto addNoteToTicket(Long id, Note noteDto) throws UserNotFoundException;

	IssuesDto updateNote(Long id, Long noteId, Note noteDto) throws UserNotFoundException;

	void deleteNote(Long id, Long noteId) throws UserNotFoundException;

	
    List<IssueReporting> getIssuesByStatus(Long orgId);

	List<IssueReporting> getIssuesByStatus(Long orgId, String search);

	List<IssuesDto> getTicketsByUserId(Long userId) throws UserNotFoundException;
	

}