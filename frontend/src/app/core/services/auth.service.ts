import { inject, Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; //Công cụ gửi HTTP REQUEST
import { Observable, tap } from 'rxjs';
import { ApiResponse, AuthenticationRequest, AuthenticationResponse } from '../models';

@Injectable({
  providedIn: 'root',
})
//đăng ký service để Angular có thể inject và sử dụng toàn ứng dụng.

// LOGIC VỀ XÁC THỰC
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/auth';

  login(credential: AuthenticationRequest): Observable<ApiResponse<AuthenticationResponse>> {
    return this.http
      .post<ApiResponse<AuthenticationResponse>>(`${this.apiUrl}/login`, credential)
      .pipe(
        tap((response) => {
          if (response.result?.access_token) {
            localStorage.setItem('access_token', response.result.access_token);
          }
        }),
      );
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  logout(): void {
    localStorage.removeItem('access_token');
  }
  
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
