export interface User {
  id: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  role: string;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface Account {
  id: number;
  accountNumber: string;
  fullName: string;
  balance: number;
  status: string;
  createdAt: string;
}

export interface Transaction {
  id: number;
  transactionId: string;
  sourceAccountNumber: string;
  destinationAccountNumber: string;
  amount: number;
  description: string;
  status: string;
  transactionTime: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}