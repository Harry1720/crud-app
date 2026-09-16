import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserForm } from '../user-form/user-form';

@Component({
  imports: [FormsModule, UserForm],
  selector: 'app-user-list',
  styleUrl: './user-list.scss',
  templateUrl: './user-list.html',
})
export class UserList {

  users = [
    {
      id: 1,
      username: 'harry',
      fullName: 'Harry Bao',
      role: 'USER',
      password: '1234'
    },
    {
      id: 2,
      username: 'john',
      fullName: 'John Nguyen',
      role: 'MAKER',
      password: '12345'
    }
  ];

  addUser(user: any) {
    user.id = this.users.length + 1;
    // this.users.push(user);
    this.users=[...this.users, user];
  }

  deleteUser(id: number){
    this.users=this.users.filter(user => user.id !== id);
    //Điều kiện user.id !== id: Điều kiện này có nghĩa là "giữ lại những user nào có ID khác với ID cần xóa".
    // Gán lại mảng (this.users = ...): Mảng mới (sau khi đã lọc bỏ user cần xóa) sẽ được gán ngược lại cho biến this.users.
  }

  editingUser: any = null; //?? Công dụng
  editUser(user: any){
    this.editingUser = {...user}; //tạo bản sao của user cần sửa để đưa data lên form
  }

  updateUser(updatedUser: any) {
    // Tìm user đang sửa trong mảng
    const index = this.users.findIndex(user => user.id === updatedUser.id);
    
    // Ghi đè thông tin mới
    if (index !== -1) {
      this.users[index] = updatedUser;
    }
    
    // Xóa trạng thái đang sửa để form trở về dạng Create
    this.editingUser = null; 
  }

}