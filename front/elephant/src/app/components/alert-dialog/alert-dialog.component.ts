import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-alert-dialog',
  templateUrl: './alert-dialog.component.html',
  styleUrls: ['./alert-dialog.component.scss']
})
export class AlertDialogComponent implements OnInit {

  message: string;
  parentDialog;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<AlertDialogComponent>,
  ) {
  }

  ngOnInit(): void {
    this.message = this.data.message;
    this.parentDialog = this.data.parentDialog;
  }

  onConfirm(): void {
    localStorage.setItem('firstTime', 'true');
    this.dialogRef.close();
    this.parentDialog.close();
  }

}
