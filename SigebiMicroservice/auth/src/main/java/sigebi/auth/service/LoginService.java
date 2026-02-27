package sigebi.auth.service;

import sigebi.auth.DTO.request.LoginRequest;
import sigebi.auth.DTO.response.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest request);
}

