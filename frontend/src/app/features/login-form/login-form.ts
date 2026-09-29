import { Component } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-login-form',
  styleUrl: './login-form.scss',
  templateUrl: './login-form.html',
})
export class LoginForm {
  loginForm = new FormGroup({
    username: new FormControl('',[

    ]),
    password: new FormControl('',[

    ])
  })

  loginSubmit(){

  }
}
