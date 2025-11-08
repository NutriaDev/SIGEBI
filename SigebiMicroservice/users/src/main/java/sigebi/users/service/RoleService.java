package sigebi.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sigebi.users.entities.RoleEntity;
import sigebi.users.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleEntity> getAllRoles(){
        return roleRepository.findAll();
    }

    public RoleEntity getRoleById(int id){
        return roleRepository.findById(id).get();
    }

    public RoleEntity saveRole(RoleEntity role){
        return roleRepository.save(role);
    }

    public void deleteRoles(int idRole){
        roleRepository.deleteById(idRole);
    }



}
