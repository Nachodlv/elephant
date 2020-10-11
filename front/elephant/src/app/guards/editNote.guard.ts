import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router, ActivatedRoute} from '@angular/router';
import {forkJoin, Observable} from 'rxjs';
import {NoteService} from '../services/note.service';
import {map} from 'rxjs/operators';
import {SnackbarService} from '../services/snackbar.service';

@Injectable({
  providedIn: 'root'
})
export class EditNoteGuard implements CanActivate {

  constructor(
    private router: Router,
    private noteService: NoteService,
    private snackBar: SnackbarService
  ) {
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const noteId = next.paramMap.get('id');

    return this.noteService.hasEditPermission(noteId).pipe(map(hasEditPermission => {
      if (hasEditPermission) {
        return true;
      } else {
        return false;
      }
    }));

    /*return forkJoin([this.noteService.startEdit(noteId), this.noteService.hasEditPermission(noteId)]).pipe(map((res) => {
      const isNotLocked = res[0];
      const hasEditPermission = res[1];
      if (isNotLocked && hasEditPermission) {
        return true;
      }
      this.snackBar.openSnackbar('No es posible editar la nota en este momento');
      this.router.navigate(['/note/', noteId]);
      return false;
    }));*/
  }

}
