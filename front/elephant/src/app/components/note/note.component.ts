import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NoteCreatorComponent} from '../note-creator/note-creator.component';
import {Note} from '../../models/note-model';
import {NoteService} from '../../services/note.service';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {SnackbarService} from '../../services/snackbar.service';
import {DeleteNoteDialogComponent} from '../delete-note-dialog/delete-note-dialog.component';

@Component({
  selector: 'app-note',
  templateUrl: './note.component.html',
  styleUrls: ['./note.component.scss']
})
export class NoteComponent implements OnInit, OnDestroy {

  notes: Note[] = [];
  loaded = false;
  filterString = '';

  noteToPrint: Note;

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
    return content.length > 300 ? content.substring(0, 300).concat('...') : content;
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

  deleteNote(note: Note): void {
    const dialogRef = this.dialog.open(DeleteNoteDialogComponent, {
      width: '400px',
      height: '230px',
      data: note,
    });

    dialogRef.afterClosed().subscribe(resNote => {
      if (resNote) {
        this.noteService.deleteNote(resNote).subscribe(resObs => {
          this.notes.splice(this.notes.indexOf(resNote), 1);
          this.snackBar.openSnackbar('La nota se ha eliminado correctamente!', 0);
        }, error => {
          this.snackBar.openSnackbar('Ha ocurrido un error y la nota no se pudo eliminar.', 0);
        });
      }
    });
  }

  printNote(note): void {
    this.noteToPrint = note;
  }
}
