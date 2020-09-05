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

@NgModule({
  declarations: [
    AppComponent,
    ExamplePipe,
    HomeComponent,
    NoteComponent,
    NoteCreatorComponent,
    NavbarComponent,
    RegisterComponent,
    EqualValidator,
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
  bootstrap: [AppComponent],
  entryComponents: [],
})
export class AppModule {
}
