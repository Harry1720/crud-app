import { Component, inject } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { CreateUserRequest } from '../../core/models';
import { RouterLink } from '@angular/router';

@Component({
  imports: [ReactiveFormsModule, RouterLink],
  selector: 'app-signup-form',
  styleUrl: './signup-form.scss',
  templateUrl: './signup-form.html',
})
export class SignupForm {
  private userService = inject(UserService)
  submitAttempted = false;

  signupForm = new FormGroup({
    firstName: new FormControl("",[
      Validators.required,
      Validators.maxLength(100),
    ]),
    lastName: new FormControl("",[
      Validators.required,
      Validators.maxLength(100),
    ]),
    username: new FormControl("", [
      Validators.required,
      Validators.maxLength(100),
      Validators.minLength(5)
    ]),
    email: new FormControl("",[
      Validators.required, 
      Validators.email
    ]),
    phoneNumber: new FormControl("",[
      Validators.required,
      Validators.pattern(/^(?:0[35789]\d{8}|\+84[35789]\d{8})$/)
    ]),
    dateOfBirth: new FormControl(""), 
    gender: new FormControl(""),
    password: new FormControl("",[
      Validators.required,
      Validators.minLength(8)
      // Others requirements
      
    ]),
    passwordRetype: new FormControl("",[
      Validators.required
    ])
    }, 
    {
      validators: this.passwordMatchValidator
    }
  )

  passwordMatchValidator(form: AbstractControl){
    // Angular yêu cầu một hàm custom validator phải nhận vào tham số kiểu AbstractControl
    // AbstractControl là interface chung cho cả FormControl, FormGroup, và FormArray
    const password = form.get('password')?.value;
    const passwordRetype = form.get('passwordRetype')?.value;
    return password !== passwordRetype ? {passwordMismatch: true} : null;
  }
    /**AbstractControl là lớp cơ sở (base class) trong Angular Reactive Forms.

      Bản chất: Cả ba thành phần quản lý form trong Angular gồm FormControl (đại diện cho một ô input), FormGroup (nhóm nhiều ô input) và FormArray (danh sách các input) đều kế thừa từ AbstractControl. Nó cung cấp các thuộc tính và phương thức chung như .value, .valid, .invalid, .errors, hay hàm .get(). */

  onSubmitSignUp(){
    this.submitAttempted = true;

    if(this.signupForm.invalid){
      this.signupForm.markAllAsTouched();
      return;
    }
    //markAllAsTouched() sẽ làm hiện các thông báo lỗi cho những trường đang sai.

    const formData = this.signupForm.value;

    const requestUserData: CreateUserRequest = {
      username: formData.username!,
      password: formData.password!,
      firstName: formData.firstName!,
      lastName: formData.lastName!,
      email: formData.email!,
      phoneNumber: formData.phoneNumber!,
      dateOfBirth: formData.dateOfBirth ? formData.dateOfBirth : undefined,
      gender: formData.gender ? formData.gender : undefined
    }
    /*Dấu ! trong TypeScript được gọi là Non-null Assertion Operator (Toán tử khẳng định khác null/undefined). => chắc chắn biến này có giá trị, nó sẽ không bao giờ là null hay undefined ở thời điểm này, hãy bỏ qua cảnh báo lỗi kiểu dữ liệu. */

    this.userService.createUser(requestUserData).subscribe({
      next: (res) => {
        alert("Đăng ký thành công! " + res.message);
        this.signupForm.reset();
        this.submitAttempted = false;
      },
      error: (err) => {
        alert("Đăng ký thất bại: " + (err.error?.message || err.message));
        console.error("Lỗi tạo user: " + err);
      }
    })
  }
}
