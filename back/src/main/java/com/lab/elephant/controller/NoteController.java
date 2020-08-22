package com.lab.elephant.controller;

import com.lab.elephant.model.Note;
import com.lab.elephant.service.NoteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping(value = "/note")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/new")
    public Note addNote(@RequestBody Note note) throws Exception {
        note.setTitle("Nueva Nota");
        note.setContent("");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        note.setCreated(timestamp);

        return noteService.addNote(note);
    }

}
