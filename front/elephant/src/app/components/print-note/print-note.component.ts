import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {Note} from '../../models/note-model';

@Component({
  selector: 'app-print-note',
  templateUrl: './print-note.component.html',
  styleUrls: ['./print-note.component.scss']
})
export class PrintNoteComponent implements OnInit, AfterViewInit {

  @Input() noteToPrint: Note;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.print();
  }

  print(): void {
    document.body.innerHTML = document.getElementById('printable').innerHTML;
    window.focus();
    window.print();
    window.close();
    location.reload();
  }

}
