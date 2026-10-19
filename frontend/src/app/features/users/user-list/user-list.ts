import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserForm } from '../user-form/user-form';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models';
import { RouterLink } from '@angular/router';

@Component({
  imports: [FormsModule, UserForm, RouterLink],
  selector: 'app-user-list',
  styleUrl: './user-list.scss',
  templateUrl: './user-list.html',
})
export class UserList {
  private userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef);

  users: User[] = [];
  editingUser: any = null;
  loadingUsers = false;
  usersLoadError = '';

  ngOnInit(): void{
    this.loadUsers();
  }

  loadUsers(): void{
    this.loadingUsers = true;
    this.usersLoadError = '';
    this.userService.getUsers().subscribe({
      next: (userList) => {
          this.users = userList;
          this.loadingUsers = false;
          this.cdr.detectChanges();
      },
      error: (err) => {
          this.loadingUsers = false;
          this.usersLoadError = err.error?.message || `Không tải được danh sách users (HTTP ${err.status}).`;
          console.error('Lỗi tải danh sách users:', err);
      },
    })
  }
  
  addUser(userData: any): void {

    // const payload = {
    //   ...userData,
    //   userData.firstName || "",
    //   userData.lastName || "",
    //   email: userData.email || `${userData.username}@example.com` // Tránh lỗi email blank
    // }

  }

  deleteUser(id: string){
    this.users=this.users.filter(user => user.id !== id);
    //Điều kiện user.id !== id: Điều kiện này có nghĩa là "giữ lại những user nào có ID khác với ID cần xóa".
    // Gán lại mảng (this.users = ...): Mảng mới (sau khi đã lọc bỏ user cần xóa) sẽ được gán ngược lại cho biến this.users.
  }

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
