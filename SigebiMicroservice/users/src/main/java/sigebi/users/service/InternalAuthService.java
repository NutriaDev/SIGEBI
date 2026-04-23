package sigebi.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sigebi.users.dto_response.UserAuthDataResponse;
import sigebi.users.dto_response.UserBasicResponse;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.ResourceNotFoundException;
import sigebi.users.exception.UserNotFoundException;
import sigebi.users.repository.UsersRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class InternalAuthService {
    private final UsersRepository usersRepository;
    private final EncryptService encryptService;


    public UserAuthDataResponse validateCredentials(String email, String rawPassword) {

        UserEntity user = usersRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid credentials"
                        )
                );

        if (!user.getActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User inactive"
            );
        }

        if (!encryptService.verifyHash(rawPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        return UserAuthDataResponse.builder()
                .userId(user.getIdUsers())
                .email(user.getEmail())
                .name(user.getName())
                .roles(List.of(user.getRole().getNameRole()))
                .build();
    }


    public UserAuthDataResponse getUserById(Long id) {

        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserAuthDataResponse.builder()
                .userId(user.getIdUsers())
                .email(user.getEmail())
                .name(user.getName()) // o firstName + lastName
                .roles(List.of(user.getRole().getNameRole()))
                .build();
    }

    public UserBasicResponse findBasicByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        UserEntity user = usersRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return new UserBasicResponse(
                user.getIdUsers(),
                user.getName(),
                user.getEmail(),
                user.getActive()
        );
    }

    @Transactional
    public void updateHashedPassword(Long userId, String hashedPassword) {
        UserEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));


        user.setPasswordHash(hashedPassword);
        usersRepository.save(user);
    }
}
