import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NoteCreatorComponent} from '../note-creator/note-creator.component';

@Component({
  selector: 'app-note',
  templateUrl: './note.component.html',
  styleUrls: ['./note.component.scss']
})
export class NoteComponent implements OnInit {

  constructor(public dialog: MatDialog) {
  }

  openDialog(): void {
    this.dialog.open(NoteCreatorComponent, {
      width: '400px',
      height: '230px',
    });
  }

  ngOnInit(): void {
  }
}
