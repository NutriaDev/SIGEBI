package sigebi.users.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sigebi.users.dto_request.UsersRequest;
import sigebi.users.dto_response.CompanyResponse;
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

    private void validateMinimumAge(Date birthDate) {

        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required");
        }

        LocalDate birthLocalDate = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate today = LocalDate.now();

        if (birthLocalDate.isAfter(today)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }

        if (Period.between(birthLocalDate, today).getYears() < 14) {
            throw new IllegalArgumentException("User must be at least 14 years old");
        }
    }


    public UserResponse createUser(@Valid UsersRequest request) {


        validateMinimumAge(request.getBirthDate());

        String email = normalizeEmail(request.getEmail());
        validateEmailDomain(email);

        if (usersRepository.existsByEmail(email)) {
            throw new EmailException("Email already exists");
        }

        if (usersRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        CompanyEntity company = companyService.getCompanyById(request.getCompanyId());
        RoleEntity role = roleService.getRoleById(request.getIdRole());
        String hashedPassword = encryptService.createdHash(request.getPassword());

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .lastname(request.getLastName())
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .id(request.getId())
                .email(email)
                .companyId(company)
                .password(hashedPassword)
                .active(request.getActive())
                .role(role)
                .build();

        return mapToResponse(usersRepository.save(user));
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
    public UserResponse updateUser(Long id, @Valid UsersRequest request) {

        UserEntity existing = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        // 1️⃣ Edad mínima (solo si viene informada)
        if (request.getBirthDate() != null) {
            validateMinimumAge(request.getBirthDate());
            existing.setBirthDate(request.getBirthDate());
        }

        // 2️⃣ Email (normalizar + validar dominio + unicidad)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = normalizeEmail(request.getEmail());
            validateEmailDomain(email);

            if (!email.equals(existing.getEmail())
                    && usersRepository.existsByEmail(email)) {
                throw new EmailException("Email already exists");
            }

            existing.setEmail(email);
        }

        // 3️⃣ Teléfono (validar unicidad)
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (!request.getPhone().equals(existing.getPhone())
                    && usersRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("Phone number already exists");
            }

            existing.setPhone(request.getPhone());
        }

        // 4️⃣ Empresa obligatoria
        if (request.getCompanyId() != null){
            CompanyEntity company = companyService.getCompanyById(request.getCompanyId());
            existing.setCompanyId(company);
        }

        // 5️⃣ Rol
        if (request.getIdRole() != null) {
            RoleEntity role = roleService.getRoleById(request.getIdRole());
            existing.setRole(role);
        }

        // 6️⃣ Password (solo si se envía)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(encryptService.createdHash(request.getPassword()));
        }

        // 7️⃣ Datos básicos
        existing.setName(request.getName());
        existing.setLastname(request.getLastName());
        existing.setActive(request.getActive());

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


    //Asignar roles o cambiar permisos

    // ========================================
    // 🔹 Conversor interno (Entity → Response)
    // ========================================

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
                .build();
    }
}
