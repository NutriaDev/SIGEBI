package sigebi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.auth.DTO.response.LoginResponse;
import sigebi.auth.entities.SessionEntity;
import sigebi.auth.service.JwtService;
import sigebi.auth.service.SessionService;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl {
    private final SessionService sessionService;
    private final JwtService jwtService;

    public LoginResponse login(Long userId) {

        SessionEntity session = sessionService.create(userId);

        return LoginResponse.builder()
                .accessToken(jwtService.generate(userId, session.getId()))
                .expiresAt(jwtService.getExpiration())
                .sessionId(session.getId())
                .build();
    }
}
