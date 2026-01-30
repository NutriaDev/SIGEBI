package sigebi.auth.service;

import sigebi.auth.DTO.response.LoginResponse;

public interface LoginService {
    LoginResponse login(Long userId);
}

