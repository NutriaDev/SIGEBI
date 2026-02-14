package sigebi.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.users.dto_response.UserAuthDataResponse;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternalAuthService {
    private final UsersRepository usersRepository;
    private final EncryptService encryptService;

    public UserAuthDataResponse validateCredentials(String email, String rawPassword) {

        UserEntity user = usersRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getActive()) {
            throw new RuntimeException("User inactive");
        }

        if (!encryptService.verifyHash(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }


        return UserAuthDataResponse.builder()
                .userId(user.getIdUsers())
                .roles(List.of(user.getRole().getNameRole()))
                .build();

    }

}
