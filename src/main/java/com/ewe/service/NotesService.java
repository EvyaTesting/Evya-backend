package com.ewe.service;

import com.ewe.exception.UserNotFoundException;
import com.ewe.pojo.Notes;

import java.util.List;

public interface NotesService {

    Notes addNote(Long employeeId, Long recipientId, Long taskId, Long issueId,
    
    String title, String description, String createdByRole) throws UserNotFoundException;

    Notes updateNoteById(Long noteId, String title, String description);

    void deleteNoteById(Long noteId);

    List<Notes> getNotesByTaskId(Long taskId) throws UserNotFoundException;

    // Add this method if you want to get note by ID
    Notes getNoteById(Long noteId) throws UserNotFoundException;

	List<Notes> getNotesByIssueId(Long issueId) throws UserNotFoundException;
}