package com.ajaz.userservice.services;

import com.ajaz.userservice.models.Role;
import com.ajaz.userservice.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }
    public Role createRole(String name) {
        Role role = new Role();
        role.setName(name);

        return roleRepository.save(role);
    }
}
