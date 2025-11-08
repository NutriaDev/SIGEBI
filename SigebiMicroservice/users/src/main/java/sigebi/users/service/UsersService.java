package sigebi.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sigebi.users.entities.UserEntity;
import sigebi.users.repository.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public UserEntity createUser(UserEntity user){
        return usersRepository.save(user);
    }

    public List<UserEntity> getAllUsers(){
        return usersRepository.findAll();
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
            UserEntity updatedUser
    ){
        return usersRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedUser.getName());
                    existing.setLastname(updatedUser.getLastname());
                    existing.setPhone(updatedUser.getPhone());
                    existing.setRole(updatedUser.getRole());
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


}
