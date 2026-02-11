package sigebi.auth.service;

import sigebi.auth.DTO.request.LogoutRequest;
import sigebi.auth.DTO.response.LogoutResponse;

public interface LogoutServive {
    LogoutResponse logout(LogoutRequest request);
}
