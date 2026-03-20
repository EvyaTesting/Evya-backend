package com.ewe.controller;

import com.ewe.exception.UserNotFoundException;
import com.ewe.form.NoteDTO;
import com.ewe.pojo.Notes;
import com.ewe.service.NotesService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ewe.messages.ResponseMessage;

import java.util.List;
@RestController
@RequestMapping("/services/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @ApiOperation(value = "Add a new note for an issue")
    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> addNote(@RequestBody NoteDTO noteRequest) 
            throws UserNotFoundException {
        Notes note = notesService.addNote(
            noteRequest.getEmployeeId(),
            noteRequest.getRecipientId(),
            noteRequest.getTaskId(),
            noteRequest.getIssueId(),
            noteRequest.getTitle(),
            noteRequest.getDescription(),
            noteRequest.getCreatedByRole()
        );
        String msg = "Note added successfully with ID: " + note.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(msg));
    }

    @ApiOperation(value = "Update a note by ID")
    @PutMapping("/update/{noteId}")
    public ResponseEntity<ResponseMessage> updateNote(
            @PathVariable Long noteId,
            @RequestBody NoteDTO noteRequest) throws UserNotFoundException {
        Notes updatedNote = notesService.updateNoteById(noteId, noteRequest.getTitle(), noteRequest.getDescription());
        String msg = "Note updated successfully with ID: " + updatedNote.getId();
        return ResponseEntity.ok(new ResponseMessage(msg));
    }

    @ApiOperation(value = "Delete a note by ID")
    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<ResponseMessage> deleteNote(@PathVariable Long noteId) 
            throws UserNotFoundException {
        notesService.deleteNoteById(noteId);
        String msg = "Note deleted successfully with ID: " + noteId;
        return ResponseEntity.ok(new ResponseMessage(msg));
    }

    @ApiOperation(value = "Get notes by Task ID")
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Notes>> getNotesByTask(@PathVariable Long taskId) 
            throws UserNotFoundException {
        List<Notes> notes = notesService.getNotesByTaskId(taskId);
        return ResponseEntity.ok(notes);
    }
    @ApiOperation(value = "Get all notes for an issue")
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<List<Notes>> getNotesByIssue(@PathVariable Long issueId) throws UserNotFoundException {
        List<Notes> notes = notesService.getNotesByIssueId(issueId);
        return ResponseEntity.ok(notes);
    }
    
    @ApiOperation(value = "Get note by ID")
    @GetMapping("/{noteId}")
    public ResponseEntity<Notes> getNoteById(@PathVariable Long noteId) 
            throws UserNotFoundException {
        Notes note = notesService.getNoteById(noteId);
        return ResponseEntity.ok(note);
    }
}