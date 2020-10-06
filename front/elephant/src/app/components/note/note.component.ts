import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NoteCreatorComponent} from '../note-creator/note-creator.component';
import {Note} from '../../models/note-model';
import {NoteService} from '../../services/note.service';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-note',
  templateUrl: './note.component.html',
  styleUrls: ['./note.component.scss']
})
export class NoteComponent implements OnInit, OnDestroy {

  notes: Note[] = [];
  loaded = false;
  filterString = '';

  notesSubscription: Subscription;

  constructor(
    private dialog: MatDialog,
    private noteService: NoteService,
    private router: Router,
    private snackBar: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.loadNotes();
  }

  ngOnDestroy(): void {
    this.notesSubscription?.unsubscribe();
  }

  loadNotes(): void {
    this.notesSubscription = this.noteService.getAllNotes().subscribe(res => {
      this.notes = this.resolveNotesData(res);
      this.loaded = true;
    }, error => {
      console.error(error);
      this.snackBar.openSnackbar('¡Ha ocurrido un error al cargar las notas!', 0);
    });
  }

  resolveNotesData(notes): Note[] {
    return notes.map(note => {
      return {...note, content: this.truncateContent(note.content)};
    });
  }

  truncateContent(content): string {
    return content.substring(0, 300).concat('...');
  }

  openDialog(): void {
    this.dialog.open(NoteCreatorComponent, {
      width: '400px',
      height: '230px',
    });
  }

  openNote(note): void {
    console.log(note);
    this.router.navigate(['/note/', note.uuid]);
  }
}
