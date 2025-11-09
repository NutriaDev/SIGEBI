package sigebi.users.service;

import jakarta.validation.Valid;
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
    private RoleService roleService;
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
        return usersRepository.findAll(
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        )
    }

    public UserEntity getUserById(Long id){
        Optional<UserEntity> userOptional = usersRepository.findById(id);
        return userOptional.orElse(null);
    }

    public Optional<UserEntity> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    //findAllByStatus pendiente en master class

    //existsByEmail pendiente en master class

    //Editar información de usuario
    public UserEntity updateUser(
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

                    return usersRepository.save(existing);


                })
                .orElseThrow(() -> new RuntimeException("user not found"));
    }

    //Activar / desactivar usuarios (soft delete)

    public void deactiveUser(Long id){
        usersRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            usersRepository.save(user);
        });
    }

    public void deleteUser(Long id){
        usersRepository.deleteById(id);
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
                .company(entity.getIdCompany())
                .active(entity.getActive())
                .roleName(entity.getRole() != null ? entity.getRole().getNameRole() : null)
                .build();
    }


}
