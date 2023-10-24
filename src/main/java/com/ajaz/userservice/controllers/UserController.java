package com.ajaz.userservice.controllers;


import com.ajaz.userservice.dtos.SetUserRolesRequestDto;
import com.ajaz.userservice.dtos.UserDto;
import com.ajaz.userservice.models.User;
import com.ajaz.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("id") Long userId){
        UserDto userDto = userService.getUseDetails(userId);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UserDto> setUserRoles(@PathVariable("id") Long userId, @RequestBody SetUserRolesRequestDto request){
        //return userService.setUserRoles(userId, );
        UserDto userDto = userService.setUserRoles(userId, request.getRoleIds());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }


}
