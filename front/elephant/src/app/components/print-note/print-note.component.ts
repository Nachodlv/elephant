import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {Note} from '../../models/note-model';
import {SnackbarService} from '../../services/snackbar.service';

@Component({
  selector: 'app-print-note',
  templateUrl: './print-note.component.html',
  styleUrls: ['./print-note.component.scss']
})
export class PrintNoteComponent implements OnInit, AfterViewInit {

  @Input() noteToPrint: Note;

  public note: Note;

  constructor(
    private snackBar: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.note = this.resolveNote(this.noteToPrint);
  }

  ngAfterViewInit(): void {
    this.print();
  }

  resolveNote(note): Note {
    const timeStamp = note.created.split('T');
    const date = timeStamp[0];
    return {...note, created: date};
  }

  print(): void {
    document.body.innerHTML = document.getElementById('printable').innerHTML;
    try {
      window.focus();
      window.print();
      window.close();
    } catch {
      this.snackBar.openSnackbar('Ha ocurrido un error al imprimir la nota!', 0);
    }
    location.reload();
  }

}
