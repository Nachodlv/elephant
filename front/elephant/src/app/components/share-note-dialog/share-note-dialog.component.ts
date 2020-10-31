import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
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
      this.snackBar.openSnackbar('¡Se ha compartido con éxito!', 0);
      this.dialogRef.close();
    }, error => {
      if (error.status === 404) {
        this.snackBar.openSnackbar('No existe un usuario con ese email', 0);
      } else if (error.status === 403) {
        this.snackBar.openSnackbar('El usuario ya tiene un permiso sobre esta nota', 0);
      } else {
        this.snackBar.openSnackbar('¡Ha ocurrido un error, vuelva a intentarlo!', 0);
      }
    });
  }
}
