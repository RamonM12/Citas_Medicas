package com.ramon.auth.service;

import com.ramon.auth.dto.LoginRequest;
import com.ramon.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse autenticar(LoginRequest request) throws Exception;
}
