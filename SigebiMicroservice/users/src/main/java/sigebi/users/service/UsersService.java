package sigebi.users.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sigebi.users.dtoRequest.UsersRequest;
import sigebi.users.dtoResponse.UserResponse;
import sigebi.users.entities.RoleEntity;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EncryptService encryptService;

    public UserEntity createUser(@Valid UsersRequest request){
        RoleEntity role = roleService.getRoleById(request.getIdRole());

        String hashedPassword = encryptService.createdHash(request.getPassword());

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .lastname(request.getLastName())
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .id(request.getId())
                .email(request.getIdCompany())
                .idCompany(request.getIdCompany())
                .password(hashedPassword)
                .active(request.getActive())
                .role(role)
                .build();

        return usersRepository.save(user);
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
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public Optional<UserResponse> getUserByEmail(String email) {

        return usersRepository.findByEmail(email).map(this::mapToResponse);
    }

    //findAllByStatus pendiente en master class

    //Editar información de usuario
    public UserResponse updateUser(
            Long id,
            UsersRequest updatedUserRequest
    ){
        return usersRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedUserRequest.getName());
                    existing.setLastname(updatedUserRequest.getLastName());
                    existing.setPhone(updatedUserRequest.getPhone());
                    existing.setRole(roleService.getRoleById((updatedUserRequest.getIdRole())));

                    if(updatedUserRequest.getPassword() != null && !updatedUserRequest.getPassword().isBlank()) {
                        existing.setPassword(encryptService.createdHash(updatedUserRequest.getPassword()));
                    }

                    existing.setIdCompany(updatedUserRequest.getIdCompany());
                    existing.setActive(updatedUserRequest.getActive());

                    UserEntity updatedUser = usersRepository.save(existing);
                    return mapToResponse(updatedUser);


                })
                .orElseThrow(() -> new RuntimeException("user not found"));
    }

    //Activar / desactivar usuarios (soft delete)

    public UserResponse toggleUserStatus(Long id, boolean active){
        return usersRepository.findById(id)
                .map(user -> {
                    user.setActive(active);
                    UserEntity updated = usersRepository.save(user);
                    return mapToResponse(updated);
        })
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

    }

    public UserResponse deleteUser(Long id) {
        return usersRepository.findById(id)
                .map(user -> {
                    usersRepository.delete(user); // ✅ elimina con seguridad
                    return mapToResponse(user);   // Devuelve el usuario eliminado (por auditoría)
                })
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
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
                .idCompany(entity.getIdCompany())
                .active(entity.getActive())
                .roleName(entity.getRole() != null ? entity.getRole().getNameRole() : null)
                .build();
    }



}
