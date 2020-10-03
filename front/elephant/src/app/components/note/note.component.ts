import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NoteCreatorComponent} from '../note-creator/note-creator.component';
import {Note} from '../../models/note-model';
import {NoteService} from '../../services/note.service';

@Component({
  selector: 'app-note',
  templateUrl: './note.component.html',
  styleUrls: ['./note.component.scss']
})
export class NoteComponent implements OnInit {

  notes: Note[] = [];
  loaded = false;
  filterString = '';

  constructor(private dialog: MatDialog,
              private noteService: NoteService,
  ) {
  }

  ngOnInit(): void {
    this.noteService.getAllNotes().subscribe(res => {
      this.notes = res;
      this.loaded = true;
    });
  }

  openDialog(): void {
    this.dialog.open(NoteCreatorComponent, {
      width: '400px',
      height: '230px',
    });
  }

  openNote(note): void {
    console.log(note);
  }
}
