package com.ajaz.userservice.controllers;

import com.ajaz.userservice.dtos.*;
import com.ajaz.userservice.exceptions.NotFoundException;
import com.ajaz.userservice.models.SessionStatus;
import com.ajaz.userservice.services.AuthService;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) throws NotFoundException{
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) throws NotFoundException{
        return authService.logout(request.getUserId(), request.getToken());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request){
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponseDto> validateToken(@RequestBody ValidateTokenRequestDto request) throws NotFoundException{

        ValidateResponseDto response = authService.validate(request.getUserId(), request.getToken());

        if(response.getSessionStatus().equals(SessionStatus.ENDED)){
            return null;
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<String> handleExceptions(NotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
