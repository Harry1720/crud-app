import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { RouterLink } from '@angular/router';

@Component({
  imports: [ReactiveFormsModule, RouterLink],
  selector: 'app-login-form',
  styleUrl: './login-form.scss',
  templateUrl: './login-form.html',
})
export class LoginForm {
  private authService = inject(AuthService);
  submitAttempted = false;

  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required])
  });

  loginSubmit(){
    this.submitAttempted = true;
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const credentials = {
      username: this.loginForm.controls.username.value!,
      password: this.loginForm.controls.password.value!
    };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        if (response.result?.isAuthenticated) {
          alert('Đăng nhập thành công!');
          this.loginForm.reset();
        } else {
          alert(response.message || 'Đăng nhập thất bại.');
        }
      },
      error: (err) => {
        alert('Đăng nhập thất bại: ' + (err.error?.message || err.message));
        console.error('Lỗi đăng nhập:', err);
      }
    });
  }
}
