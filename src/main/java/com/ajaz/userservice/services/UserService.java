package com.ajaz.userservice.services;

import com.ajaz.userservice.dtos.UserDto;
import com.ajaz.userservice.models.Role;
import com.ajaz.userservice.models.User;
import com.ajaz.userservice.repositories.RoleRepository;
import com.ajaz.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    public UserDto getUseDetails(Long userId){
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isEmpty()){
            return null;
        }

        User user = userOptional.get();

        UserDto userDto = UserDto.from(user);

        return userDto;


    }

    public UserDto setUserRoles(Long userId, List<Long> roleIds) {

        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isEmpty()){
            return null;
        }

        User user = userOptional.get();
        List<Role> userRoles = roleRepository.findAllByIdIn(roleIds);

        Set<Role> rolesSet = new HashSet<>();

        userRoles.forEach(e -> rolesSet.add(e));
        user.setRoles(rolesSet);

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);

    }
}
