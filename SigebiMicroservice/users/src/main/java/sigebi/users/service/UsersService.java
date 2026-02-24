package sigebi.users.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sigebi.users.dto_request.CreateUsersRequest;
import sigebi.users.dto_request.UpdateUserRequest;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.entities.CompanyEntity;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.exception.BusinessException;
import sigebi.users.exception.EmailException;
import sigebi.users.exception.UserNotFoundException;
import sigebi.users.repository.UsersRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EncryptService encryptService;

    //validators

    private static final Set<String> BLOCKED_EMAIL_DOMAINS = Set.of(
            "yopmail.com",
            "guerrillamail.com",
            "tempmail.com",
            "mailinator.com"
    );

    private void validateName(String value, String fieldName) {

        if (value == null || value.isBlank()) {
            throw new BusinessException(fieldName + " is required");
        }

        if (!value.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$")) {
            throw new BusinessException(fieldName + " must contain only letters");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private void validateEmailDomain(String email) {

        if (email == null || !email.contains("@")) {
            throw new BusinessException("Formato de correo inválido");
        }

        String domain = email.substring(email.lastIndexOf("@") + 1).toLowerCase();

        if (BLOCKED_EMAIL_DOMAINS.contains(domain)) {
            throw new BusinessException("No se permiten correos temporales o desechables");
        }
    }

    private static final int MIN_AGE = 18;

    private void validateMinimumAge(Date birthDate) {
        if (birthDate == null) {
            throw new BusinessException("Birth date is required");
        }

        LocalDate birth = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        int age = Period.between(birth, LocalDate.now()).getYears();

        if (age < MIN_AGE) {
            throw new BusinessException("User must be at least " + MIN_AGE + " years old");
        }
    }


    private void validatePasswordSecurity(String password) {
        if (password.length() < 8) {
            throw new BusinessException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new BusinessException("Password must contain at least one number");
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new BusinessException("Password must contain at least one special character");
        }
    }



    public UserResponse createUser(@Valid CreateUsersRequest request) {

        validateName(request.getName(), "Name");
        validateName(request.getLastName(), "Last name");

        validatePasswordSecurity(request.getPassword());

        validateMinimumAge(request.getBirthDate());

        String email = normalizeEmail(request.getEmail());
        validateEmailDomain(email);

        if (usersRepository.existsByEmail(email)) {
            throw new EmailException("Email already exists");
        }

        if (usersRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Phone number already exists");
        }

        CompanyEntity company = companyService.getCompanyById(request.getCompanyId());
        RoleEntity role = roleService.getRoleById(request.getIdRole());

        String hashedPassword = encryptService.createdHash(request.getPassword());

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setLastname(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        user.setPhone(request.getPhone());
        user.setId(request.getId());
        user.setEmail(email);
        user.setCompanyId(company);
        user.setPasswordHash(hashedPassword);
        user.setActive(request.getActive());
        user.setRole(role);

        UserEntity savedUser = usersRepository.save(user);
        return mapToResponse(savedUser);
    }



    public List<UserResponse> getAllUsers(){
        return usersRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

    }

    public UserResponse getUserById(Long id){
        return usersRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
    }

    public Optional<UserResponse> getUserByEmail(String email) {

        String normalizedEmail = normalizeEmail(email);
        validateEmailDomain(normalizedEmail);

        return usersRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .map(this::mapToResponse);
    }


    //controller
    public List<UserResponse> findUsersByActive(Boolean active){
        return usersRepository.findAllByActive(active)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //Editar User

    private UserEntity findActiveUserOrThrow(Long id) {
        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + id
                ));

        if (!user.getActive()) {
            throw new BusinessException("Cannot update an inactive user.");
        }

        return user;
    }

    private void updateBasicInfo(UserEntity existing, UpdateUserRequest request) {

        if (request.getName() != null) {
            validateName(request.getName(), "Name");
            existing.setName(request.getName());
        }

        if (request.getLastName() != null) {
            validateName(request.getLastName(), "Last name");
            existing.setLastname(request.getLastName());
        }

        if (request.getBirthDate() != null) {
            validateMinimumAge(request.getBirthDate());
            existing.setBirthDate(request.getBirthDate());
        }
    }

    private void updateContactInfo(UserEntity existing, UpdateUserRequest request) {

        if (request.getEmail() != null && !request.getEmail().isBlank()) {

            String email = normalizeEmail(request.getEmail());
            String existingEmail = normalizeEmail(existing.getEmail());

            validateEmailDomain(email);

            if (!email.equals(existingEmail)
                    && usersRepository.existsByEmail(email)) {
                throw new EmailException("Email already exists.");
            }

            existing.setEmail(email);
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {

            if (!request.getPhone().equals(existing.getPhone())
                    && usersRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException("Phone number already exists.");
            }

            existing.setPhone(request.getPhone());
        }
    }

    private void validateImmutableFields(UserEntity existing, UpdateUserRequest request) {

        if (request.getCompanyId() != null &&
                !request.getCompanyId().equals(existing.getCompanyId().getId())) {
            throw new BusinessException(
                    "Company cannot be changed. User must be deactivated instead."
            );
        }

        if (request.getIdRole() != null) {
            throw new BusinessException(
                    "Role cannot be modified. Create a new user instead."
            );
        }
    }

    private void updatePassword(UserEntity existing, UpdateUserRequest request) {

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            validatePasswordSecurity(request.getPassword());
            existing.setPasswordHash(
                    encryptService.createdHash(request.getPassword())
            );
        }
    }

    private void updateActiveStatus(UserEntity existing, UpdateUserRequest request) {

        if (request.getActive() != null) {
            existing.setActive(request.getActive());
        }
    }

    public UserResponse updateUser(Long id, @Valid UpdateUserRequest request) {

        UserEntity existing = findActiveUserOrThrow(id);

        updateBasicInfo(existing, request);
        updateContactInfo(existing, request);
        validateImmutableFields(existing, request);
        updatePassword(existing, request);
        updateActiveStatus(existing, request);

        return mapToResponse(usersRepository.save(existing));
    }



    //Activar / desactivar usuarios (soft delete)

    public UserResponse toggleUserStatus(Long id, boolean active) {

        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + id
                ));

        if (user.getActive().equals(active)) {
            throw new BusinessException(
                    active ? "User is already active." : "User is already inactive."
            );
        }

        // Protección a SUPERADMIN
        if (user.getRole().getNameRole().equalsIgnoreCase("SUPERADMIN")
                && !active) {
            throw new BusinessException(
                    "Superadmin cannot be deactivated."
            );
        }

        user.setActive(active);

        UserEntity updated = usersRepository.save(user);
        return mapToResponse(updated);
    }

    public UserResponse deleteUser(Long id) {

        UserEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + id
                ));

        if (user.getActive()) {
            throw new BusinessException(
                    "User must be deactivated before deletion."
            );
        }

        if (user.getRole().getNameRole().equalsIgnoreCase("SUPERADMIN")) {
            throw new BusinessException(
                    "Superadmin cannot be deleted."
            );
        }

        usersRepository.delete(user);

        return mapToResponse(user);
    }




    private UserResponse mapToResponse(UserEntity entity){
        return UserResponse.builder()
                .idUsers(entity.getIdUsers())
                .name(entity.getName())
                .lastname(entity.getLastname())
                .birthDate(entity.getBirthDate())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .id(entity.getId())
                .companyId(entity.getCompanyId() != null ? entity.getCompanyId().getId(): null)
                .active(entity.getActive())
                .roleName(entity.getRole() != null ? entity.getRole().getNameRole() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
