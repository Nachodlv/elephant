import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Router} from '@angular/router';

@Component({
  selector: 'app-delete-note-dialog',
  templateUrl: './delete-note-dialog.component.html',
  styleUrls: ['./delete-note-dialog.component.scss']
})
export class DeleteNoteDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DeleteNoteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public note: any,
  ) {
  }

  ngOnInit(): void {
    console.log(this.note);
  }

  cancel(): void {
    this.dialogRef.close();
  }

  deleteNote(): void {
    this.dialogRef.close(this.note);
  }
}
