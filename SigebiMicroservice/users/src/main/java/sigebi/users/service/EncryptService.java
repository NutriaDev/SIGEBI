package sigebi.users.service;

public interface EncryptService {
    String createdHash(String password);
    boolean verifyHash(String rawPassword, String hashedPassword);
}
