export interface AuthenticationRequest {
  username: string;
  password?: string;
}

export interface AuthenticationResponse {
  access_token: string;
  isAuthenticated: boolean;
  expiryTime?: string;
}
