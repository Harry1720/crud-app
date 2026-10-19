import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { User } from '../../../core/models';
import { UserService } from '../../../core/services/user.service';

@Component({
  imports: [RouterLink],
  selector: 'app-user-detail',
  styleUrl: './user-detail.scss',
  templateUrl: './user-detail.html',
})
export class UserDetail{
  private route = inject(ActivatedRoute);
  private userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef);

  user?: User;
  loading = true;
  error = '';

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id'); //?
    if (!id) {
      this.error = 'ID user không hợp lệ.';
      this.loading = false;
      return;
    }

    this.userService.getUser(id).subscribe({
      next: user => {
        this.user = user;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.error = err.error?.message || 'Không tải được thông tin user.';
        this.loading = false;
      }
    });
  }
}
