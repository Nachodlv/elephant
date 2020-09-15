import {Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormGroupDirective, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {NoteService} from '../../services/note.service';
import {SnackbarService} from '../../services/snackbar.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-share-note-dialog',
  templateUrl: './share-note-dialog.component.html',
  styleUrls: ['./share-note-dialog.component.scss']
})
export class ShareNoteDialogComponent implements OnInit, OnDestroy {

  shareForm: FormGroup;
  shareSubscription: Subscription;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private formBuilder: FormBuilder,
    private noteService: NoteService,
    private snackBar: SnackbarService,
    public dialogRef: MatDialogRef<ShareNoteDialogComponent>
  ) {
  }

  ngOnInit(): void {

    this.shareForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      permissionType: ['Viewer', Validators.required]
    });
  }

  ngOnDestroy(): void {
    this.shareSubscription?.unsubscribe();
  }

  onSubmit(): void {
    this.shareSubscription = this.noteService.shareNote(this.data.noteId, this.shareForm.value).subscribe(res => {
      this.snackBar.openSnackbar('¡Se ha compartido con exito!', 0);
      this.dialogRef.close();
    }, error => {
      this.snackBar.openSnackbar('¡Ha ocurrido un error, vuelva a intentarlo!', 0);
    });
  }
}
