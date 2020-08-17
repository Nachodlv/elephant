import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ExampleGuard} from './guards/example.guard';
import {HomeComponent} from './components/home/home.component';
import {RegisterComponent} from './components/register/register.component';
import {LoginComponent} from './components/login/login.component';


const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [ExampleGuard] },
  { path: 'signup', component: RegisterComponent, canActivate: [ExampleGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
