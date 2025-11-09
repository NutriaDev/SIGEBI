package sigebi.users.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sigebi.users.service.EncryptService;

import javax.crypto.*;


@Service
@RequiredArgsConstructor
public class EncryptServiceImpl implements EncryptService {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String createdHash(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verifyHash(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

}
