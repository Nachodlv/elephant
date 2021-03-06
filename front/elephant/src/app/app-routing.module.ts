import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {RegisterComponent} from './components/register/register.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {SeeNoteComponent} from './components/see-note/see-note.component';
import {NoteCreatorComponent} from './components/note-creator/note-creator.component';
import {NoteComponent} from './components/note/note.component';
import {SeeProfileComponent} from './components/see-profile/see-profile.component';
import {EditNoteComponent} from './components/edit-note/edit-note.component';
import {EditNoteGuard} from './guards/editNote.guard';
import {LoginAuthGuard} from './guards/login-auth.guard';


const routes: Routes = [
  {path: 'note/create', component: NoteCreatorComponent, canActivate: [AuthGuard]},
  {path: 'note', component: NoteComponent, canActivate: [AuthGuard]},
  {path: '', component: LoginComponent, canActivate: [LoginAuthGuard]},
  {path: 'register', component: RegisterComponent, canActivate: [LoginAuthGuard]},
  {path: 'home', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'note/:id', component: SeeNoteComponent, canActivate: [AuthGuard]},
  {path: 'profile', component: SeeProfileComponent, canActivate: [AuthGuard]},
  {path: 'note/edit/:id', component: EditNoteComponent, canActivate: [AuthGuard, EditNoteGuard]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
