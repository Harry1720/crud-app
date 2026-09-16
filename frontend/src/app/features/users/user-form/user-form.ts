import { Component, EventEmitter, Output, Input, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  imports: [FormsModule],
  selector: 'app-user-form',
  styleUrl: './user-form.scss',
  templateUrl: './user-form.html',
})
export class UserForm {
  username = '';
  fullName = '';
  password = '';
  role = 'USER';

  @Input() user: any = null;

  //@Output(): Đây là một decorator (đánh dấu) để báo cho Angular biết rằng biến userCreated này là một sự kiện đầu ra. Component cha có thể "lắng nghe" sự kiện này.
  //EventEmitter: Là một class của Angular dùng để phát ra các sự kiện.

  @Output() userCreated = new EventEmitter<any>();

  createUser() {
    const user = {
      username: this.username,
      fullName: this.fullName,
      password: this.password,
      role: this.role
    };

    this.userCreated.emit(user);

    //Khi người dùng điền xong form và bấm nút "Create", hàm createUser() chạy.
    // Sau khi gom các dữ liệu đã nhập thành 1 object user, nó gọi hàm .emit(user).
    // Hành động này giống như "phát sóng" một thông báo lên kênh userCreated, và đính kèm theo gói hàng là object user vừa được tạo.

    this.username = '';
    this.fullName = '';
    this.password = '';
    this.role = 'USER';
  }
  
  ngOnChanges(){ //?
    if(this.user){ //kiểm tra @Input user có chứa user cần sửa không, hay vẫn null?
      //Đổ dữ liệu lên form
      this.username=this.user.username;
      this.fullName=this.user.fullName;
      this.role=this.user.role;
      this.password = this.user.password;
    }
  }

  @Output() userUpdated = new EventEmitter<any>()
  updateUser(){
    const updatedUser = {
      id: this.user.id,
      username: this.username,
      fullName: this.fullName,
      password: this.password,
      role: this.role
    }
    this.userUpdated.emit(updatedUser);

    // (Tuỳ chọn) Nếu bạn muốn form tự làm trống sau khi update xong thì thêm các dòng dưới:
    this.username = '';
    this.fullName = '';
    this.password = '';
    this.role = 'USER';
  }
}
