import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ApiResponse } from "../models";
import { CreateUserRequest, UpdateUserRequest, User } from "../models";

@Injectable({
    providedIn: 'root'
})

export class UserService {
    private http = inject(HttpClient);
    private apiUrl = 'http://localhost:8080/api/users';

    // GET all users
    getUsers(): Observable<User[]>{
        return this.http.get<User[]>(this.apiUrl)
    }

    //GET 1 user by id
    getUser(id: string): Observable<User>{
        return this.http.get<User>(`${this.apiUrl}/${id}`);
    }

    //POST create user
    createUser(user: CreateUserRequest):Observable<ApiResponse<User>>{
        return this.http.post<ApiResponse<User>>(this.apiUrl, user);
    }

    //PUT update user
    updateUser(id: string, user: UpdateUserRequest): Observable<ApiResponse<User>>{
        return this.http.put<ApiResponse<User>>(`${this.apiUrl}/${id}`, user);
    }

    //DELETE user
    deleteUser(id: string): Observable<ApiResponse<void>>{
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
    }
}
