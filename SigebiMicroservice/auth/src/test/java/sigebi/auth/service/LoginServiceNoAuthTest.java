package sigebi.auth.service;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.auth.DTO.request.LoginRequest;
import sigebi.auth.client.UserInternalClient;
import sigebi.auth.service.impl.LoginServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceNoAuthTest {

    @Mock
    UserInternalClient userInternalClient;

    @InjectMocks
    LoginServiceImpl service;

    @Test
    void login_when403_throwsRuntimeException() {

        Request request = Request.create(
                Request.HttpMethod.POST,
                "/internal/auth/validate",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );

        Response response = Response.builder()
                .status(403)               // ✅ INT
                .reason("Forbidden")
                .request(request)
                .build();

        when(userInternalClient.validate(any()))
                .thenThrow(FeignException.errorStatus("validate", response));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("a");
        loginRequest.setPassword("b");

        assertThrows(RuntimeException.class,
                () -> service.login(loginRequest));
    }
}