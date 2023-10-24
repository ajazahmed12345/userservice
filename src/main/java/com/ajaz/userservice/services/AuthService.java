package com.ajaz.userservice.services;

import com.ajaz.userservice.dtos.UserDto;
import com.ajaz.userservice.exceptions.NotFoundException;
import com.ajaz.userservice.models.Role;
import com.ajaz.userservice.models.Session;
import com.ajaz.userservice.models.SessionStatus;
import com.ajaz.userservice.models.User;
import com.ajaz.userservice.repositories.RoleRepository;
import com.ajaz.userservice.repositories.SessionRepository;
import com.ajaz.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SessionRepository sessionRepository;


    public AuthService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
    }

    public ResponseEntity<UserDto> login(String email, String password) throws NotFoundException{

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new NotFoundException("user does not exist in our database.");
        }

        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new NotFoundException("Password is incorrect, try again");
        }

        String token = RandomStringUtils.randomAlphanumeric(30);

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);

        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);


        return response;
    }

    public UserDto signUp(String email, String password) {

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);


        return UserDto.from(savedUser);
    }

    public ResponseEntity<Void> logout(Long userId, String token) throws NotFoundException {
        Optional<Session> sessionOptional = sessionRepository.findByUser_IdAndToken(userId, token);

        if(sessionOptional.isEmpty()){
            throw new NotFoundException("Session does not exist for the userId");
        }

        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    public SessionStatus validate(Long userId, String token) throws NotFoundException {
        Optional<Session> sessionOptional = sessionRepository.findByUser_IdAndToken(userId, token);

        if(sessionOptional.isEmpty()){
            throw new NotFoundException("Session with id = " + userId + " and token = " + token + " does not exist.");
        }

        return SessionStatus.ACTIVE;

    }
}
