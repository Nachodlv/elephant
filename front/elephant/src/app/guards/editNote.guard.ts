import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router, ActivatedRoute} from '@angular/router';
import {forkJoin, Observable} from 'rxjs';
import {NoteService} from '../services/note.service';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EditNoteGuard implements CanActivate {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private noteService: NoteService
  ) {
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const noteId = this.route.snapshot.paramMap.get('id');

    return forkJoin([this.noteService.startEdit(noteId), this.noteService.hasEditPermission(noteId)]).pipe(map((res) => {
      const isNotLocked = res[0];
      const hasEditPermission = res[1];
      if (isNotLocked && hasEditPermission) {
        return true;
      }
      this.router.navigate(['/home']);
      return false;
    }));
  }

}
