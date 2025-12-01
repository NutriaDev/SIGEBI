package sigebi.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sigebi.users.dtoRequest.RoleRequest;
import sigebi.users.dto_response.RoleResponse;
import sigebi.users.entities.RoleEntity;
import sigebi.users.exception.RoleNotFoundException;
import sigebi.users.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Obtener todos los roles
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener rol por ID
    public RoleEntity getRoleById(int id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + id));
    }

    // Obtener roles por estado (true/false)
    public List<RoleResponse> getRolesByStatus(boolean status) {
        return roleRepository.findAllByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Guardar o actualizar un rol
    public RoleEntity saveRole(String nameRole, Boolean active, Long id) {

        RoleEntity role = RoleEntity.builder()
                .id(id)               // si viene ID → edita; si es null → crea
                .nameRole(nameRole)
                .status(active)
                .build();

        return roleRepository.save(role);
    }


    // Eliminar rol por ID
    public void deleteRole(int idRole) {
        if (!roleRepository.existsById(idRole)) {
            throw new RoleNotFoundException("Role not found with ID: " + idRole);
        }
        roleRepository.deleteById(idRole);
    }

    // Mapeo entidad → respuesta
    private RoleResponse mapToResponse(RoleEntity entity) {
        return RoleResponse.builder()
                .id(entity.getId())
                .nameRole(entity.getNameRole())
                .status(entity.getStatus())
                .build();
    }


}
