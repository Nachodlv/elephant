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

  isOwner = false;

  notesSubscription: Subscription;
  hasOwnerPermissionSubscription: Subscription;

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
    this.hasOwnerPermissionSubscription?.unsubscribe();
  }

  loadNotes(): void {
    this.notesSubscription = this.noteService.getAllNotes().subscribe(res => {
      this.notes = this.resolveNotesData(res);
      this.sortNotes(this.notes);
      this.loaded = true;
    }, error => {
      console.error(error);
      this.snackBar.openSnackbar('¡Ha ocurrido un error al cargar las notas!', 0);
      this.loaded = true;
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

  checkPermission(note): void {
    this.hasOwnerPermissionSubscription = this.noteService.hasOwnerPermission(note.uuid).subscribe(res => {
      this.isOwner = res;
    }, error => {
      console.error(error);
      this.snackBar.openSnackbar('Ha ocurrido un error al obtener los permisos sobre la nota', 0);
    });
  }

  printNote(note): void {
    this.noteToPrint = note;
  }
  duplicate(note: Note): void{
    const title = 'Copy - ' + note.title;
    this.noteService.createDuplicate(note.uuid).subscribe(res => {
      this.snackBar.openSnackbar('¡Creación de Nota duplicada exitosa!', 0);
      this.router.navigate(['/note/', res.uuid]);
      // como falta implementacion del back, la redireccion por ahora va a ser a la misma nota, una vez implementado,
      // rediccionara a la nueva nota
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error!', 0);
    });
  }
  toggleStickNote(note: Note): void {
    const index = this.notes.indexOf(note);
    if (index >= 0) {
      note.pinUp = !note.pinUp;
      this.notes[index] = note;
      this.sortNotes(this.notes);
    }
  }

  sortNotes(noteList: Note[]): void {
    noteList.sort((a: Note, b: Note) => {
      if (a.pinUp && !b.pinUp)
        return -1;
      else if (b.pinUp && !a.pinUp)
        return 1;

      if (new Date(a.created) > new Date(b.created))
        return -1;
      else
        return 1;
    });
  }
}
