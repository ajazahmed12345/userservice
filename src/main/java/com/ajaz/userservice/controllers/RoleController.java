package com.ajaz.userservice.controllers;

import com.ajaz.userservice.dtos.CreateRoleRequestDto;
import com.ajaz.userservice.models.Role;
import com.ajaz.userservice.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody CreateRoleRequestDto request){
        Role role = roleService.createRole(request.getName());

        return new ResponseEntity<>(role, HttpStatus.OK);
    }


}
