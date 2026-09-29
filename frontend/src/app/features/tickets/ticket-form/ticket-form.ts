import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms'; //? Công dụng

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-ticket-form',
  styleUrl: './ticket-form.scss',
  templateUrl: './ticket-form.html',
})
export class TicketForm {
  ticketForm = new FormGroup({
    //Tiêu đề của ticket
    title: new FormControl("", [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(100)
    ]),
    //Chi tiết ticket
    description: new FormControl('',[
      Validators.required,
      Validators.minLength(10)
    ]),
    // Mức độ ưu tiên
    priority: new FormControl('MEDIUM',[
      Validators.required
    ]),
    // Loại ticket
    category: new FormControl('BUG',[
      Validators.required
    ]),
    // Báo cho ai?
    assignee: new FormControl('',[
      Validators.required
    ])
  })

  // Tạo ticket
  createTicket(){
    if(this.ticketForm.valid){
      console.log(this.ticketForm.value);
    }
    else{
      console.log("Please check the errors."); 
    }
  }
}
