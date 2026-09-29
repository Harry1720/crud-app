import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, FormsModule, AbstractControl } from '@angular/forms';

@Component({
  imports: [ReactiveFormsModule, FormsModule],
  selector: 'app-signup-form',
  styleUrl: './signup-form.scss',
  templateUrl: './signup-form.html',
})
export class SignupForm {
  signupForm = new FormGroup({
    fullName: new FormControl("",[
      Validators.required,
      Validators.maxLength(100),
      Validators.minLength(5)      
    ]),
    username: new FormControl("", [
      Validators.required,
      Validators.maxLength(100),
      Validators.minLength(10)
    ]),
    email: new FormControl("",[
      Validators.required, 
      Validators.email
    ]),
    phoneNumber: new FormControl("",[
      Validators.required
    ]),
    password: new FormControl("",[
      Validators.required,
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
    if(password != passwordRetype){
      return {passwordMismatch: true}
    }
    return null;
  }

  onSubmitSignUp(){
    if(this.signupForm.valid){
      console.log(this.signupForm.value);
    }
    else{
      console.log("Please check the errors.")
    }
  }
}
