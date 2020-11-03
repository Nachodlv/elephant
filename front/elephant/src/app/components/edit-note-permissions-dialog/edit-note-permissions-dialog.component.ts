import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {SharedUser} from '../../models/sharedUser-model';
import {NoteService} from '../../services/note.service';
import {SnackbarService} from '../../services/snackbar.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-edit-note-permissions-dialog',
  templateUrl: './edit-note-permissions-dialog.component.html',
  styleUrls: ['./edit-note-permissions-dialog.component.scss']
})
export class EditNotePermissionsDialogComponent implements OnInit, OnDestroy {

  public permissions: SharedUser[] = [];

  getAllPermissionsSubscription: Subscription;
  editPermissionsSubscription: Subscription;

  allDeletedPermissions = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private noteService: NoteService,
    private snackBar: SnackbarService,
    public dialogRef: MatDialogRef<EditNotePermissionsDialogComponent>
  ) {
  }

  ngOnInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    this.getAllPermissionsSubscription?.unsubscribe();
    this.editPermissionsSubscription?.unsubscribe();
  }

  loadData(): void {
    this.getAllPermissionsSubscription = this.noteService.getAllPermissions(this.data.noteId).subscribe(res => {
      this.permissions = res;
    }, error => {
      console.error(error);
      this.dialogRef.close();
      this.snackBar.openSnackbar('¡Ha ocurrido un error al cargar los permisos, vuelva a intentarlo!', 0);
    });
  }

  onSubmit(): void {
    const permissionsJson = {list: this.permissions};
    this.editPermissionsSubscription = this.noteService.editPermissions(this.data.noteId, permissionsJson).subscribe(res => {
      this.snackBar.openSnackbar('¡Se han modificado los permisos con éxito!', 0);
      this.dialogRef.close();
    }, error => {
      console.error(error);
      this.snackBar.openSnackbar('¡Ha ocurrido un error al modificar los permisos, vuelva a intentarlo!', 0);
    });
  }

  onDelete(index): void {
    this.permissions[index].type = 'delete';
    this.checkAllDeletedPermissions();
  }

  checkAllDeletedPermissions(): void {
    this.allDeletedPermissions = this.permissions.every(permission => permission.type === 'delete');
  }
}
