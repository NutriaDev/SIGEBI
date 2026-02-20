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

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private void validateEmailDomain(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        if (BLOCKED_EMAIL_DOMAINS.contains(domain)) {
            throw new EmailException("Disposable email domains are not allowed");
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
    }



    public UserResponse createUser(@Valid CreateUsersRequest request) {

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

    //Editar información de usuario
    public UserResponse updateUser(Long id, @Valid UpdateUserRequest request) {

        UserEntity existing = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        /* 1️⃣ Fecha de nacimiento (editable si sigue siendo mayor de edad) */
        if (request.getBirthDate() != null) {
            validateMinimumAge(request.getBirthDate());
            existing.setBirthDate(request.getBirthDate());
        }

        /* 2️⃣ Email (editable + normalización + dominio + unicidad) */
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = normalizeEmail(request.getEmail());
            validateEmailDomain(email);

            if (!email.equals(existing.getEmail())
                    && usersRepository.existsByEmail(email)) {
                throw new EmailException("Email already exists");
            }

            existing.setEmail(email);
        }

        /* 3️⃣ Teléfono (editable + unicidad) */
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (!request.getPhone().equals(existing.getPhone())
                    && usersRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException("Phone number already exists");
            }

            existing.setPhone(request.getPhone());
        }

        /* 4️⃣ Empresa ❌ NO editable */
        if (request.getCompanyId() != null &&
                !request.getCompanyId().equals(existing.getCompanyId().getId())) {
            throw new BusinessException("Company cannot be changed. User must be deactivated instead.");
        }

        /* 5️⃣ Rol (editable) */
        if (request.getIdRole() != null) {
            RoleEntity role = roleService.getRoleById(request.getIdRole());
            existing.setRole(role);
        }

        /* 6️⃣ Password (editable + validación de seguridad) */
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            validatePasswordSecurity(request.getPassword());
            existing.setPasswordHash(
                    encryptService.createdHash(request.getPassword())
            );
        }

        /* 7️⃣ Datos básicos */
        if (request.getName() != null) {
            existing.setName(request.getName());
        }

        if (request.getLastName() != null) {
            existing.setLastname(request.getLastName());
        }

        if (request.getActive() != null) {
            existing.setActive(request.getActive());
        }

        UserEntity updatedUser = usersRepository.save(existing);
        return mapToResponse(updatedUser);
    }




    //Activar / desactivar usuarios (soft delete)

    public UserResponse toggleUserStatus(Long id, boolean active){
        return usersRepository.findById(id)
                .map(user -> {
                    user.setActive(active);
                    UserEntity updated = usersRepository.save(user);
                    return mapToResponse(updated);
        })
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

    }

    public UserResponse deleteUser(Long id) {
        return usersRepository.findById(id)
                .map(user -> {
                    usersRepository.delete(user); // ✅ elimina con seguridad
                    return mapToResponse(user);   // Devuelve el usuario eliminado (por auditoría)
                })
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
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
                .CompanyId(entity.getCompanyId() != null ? entity.getCompanyId().getId(): null)
                .active(entity.getActive())
                .roleName(entity.getRole() != null ? entity.getRole().getNameRole() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
