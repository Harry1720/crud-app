export interface User {
  id?: string;
  keycloakId?: string;
  username: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  gender?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface UserResponse {
  id: string;
  keycloakId?: string;
  username: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  gender?: string;
}

export interface CreateUserRequest {
  username: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  email: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  gender?: string;
}

export interface UpdateUserRequest {
  password?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  gender?: string;
}
