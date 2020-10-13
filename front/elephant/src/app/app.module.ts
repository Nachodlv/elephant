import {BrowserModule} from '@angular/platform-browser';
import {ErrorHandler, NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppMaterialModule} from './app-material.module';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ErrorService} from './services/error.service';
import {ExamplePipe} from './pipes/time-left.pipe';
import {HomeComponent} from './components/home/home.component';
import { NoteComponent } from './components/note/note.component';
import { NoteCreatorComponent } from './components/note-creator/note-creator.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { RegisterComponent } from './components/register/register.component';
import {EqualValidator} from './directives/equal-validator.directive';
import { MatDialogModule} from '@angular/material/dialog';
import {LoginComponent} from './components/login/login.component';
import { SeeNoteComponent } from './components/see-note/see-note.component';
import { ShareNoteDialogComponent } from './components/share-note-dialog/share-note-dialog.component';
import { CommentComponent } from './components/comment/comment.component';
import {SeeProfileComponent} from './components/see-profile/see-profile.component';
import { UpdatePasswordDialogComponent } from './components/update-password-dialog/update-password-dialog.component';
import { TagNoteComponent } from './components/tag-note/tag-note.component';
import { FilterNotePipe } from './pipes/filter-note.pipe';
import { DeleteNoteDialogComponent } from './components/delete-note-dialog/delete-note-dialog.component';
import { EditNoteComponent } from './components/edit-note/edit-note.component';
import { PrintNoteComponent } from './components/print-note/print-note.component';

@NgModule({
  declarations: [
    AppComponent,
    ExamplePipe,
    HomeComponent,
    NoteComponent,
    NoteCreatorComponent,
    NavbarComponent,
    RegisterComponent,
    LoginComponent,
    EqualValidator,
    SeeNoteComponent,
    ShareNoteDialogComponent,
    CommentComponent,
    SeeProfileComponent,
    UpdatePasswordDialogComponent,
    TagNoteComponent,
    EditNoteComponent,
    FilterNotePipe,
    DeleteNoteDialogComponent,
    PrintNoteComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    AppMaterialModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
  ],
  providers: [
    {provide: ErrorHandler, useClass: ErrorService}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
