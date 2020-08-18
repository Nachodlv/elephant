import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(private snackbar: MatSnackBar) {
  }

  public openSnackbar(message: string, ms?: number): void {
    this.snackbar.open(message, 'X', {
      duration: ms || 3000,
      verticalPosition: 'top'
    });
  }
}
