package com.ajaz.userservice.services;

import com.ajaz.userservice.dtos.UserDto;
import com.ajaz.userservice.dtos.ValidateResponseDto;
import com.ajaz.userservice.exceptions.NotFoundException;
import com.ajaz.userservice.models.Role;
import com.ajaz.userservice.models.Session;
import com.ajaz.userservice.models.SessionStatus;
import com.ajaz.userservice.models.User;
import com.ajaz.userservice.repositories.RoleRepository;
import com.ajaz.userservice.repositories.SessionRepository;
import com.ajaz.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.HttpClientErrorException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SessionRepository sessionRepository;

    private SecretKey key = null;


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

//        String token = RandomStringUtils.randomAlphanumeric(30);

        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();

        this.key = key;

        String message = "{ [email] : [ahmed@gmail.com]} ";


        byte[] content = message.getBytes(StandardCharsets.UTF_8);

        Map<String, Object> jsonForJwt = new HashMap<>();

        jsonForJwt.put("email", user.getEmail());
        jsonForJwt.put("roles", user.getRoles());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));



// Create the compact JWS:
        String token = Jwts.builder()
                        .claims(jsonForJwt)
                        .signWith(key, alg)
                        .compact();

        System.out.println(key);

// Parse the compact JWS:
//        content = Jwts.parser().verifyWith(key).build().parseSignedContent(token).getPayload();


//        auth-token%3AeyJjdHkiOiJ0ZXh0L3BsYWluIiwiYWxnIjoiSFMyNTYifQ.eyBbZW1haWxdIDogW2FobWVkQGdtYWlsLmNvbV0g.9bjJWAE_-HCdSYZ_tl-cTu2jOqMr-8A85ptpWWcNJ1w
// auth-token%3AeyJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVkQXQiOjE2OTgyMTQ0NTI3NzksInJvbGVzIjpbXSwiZXhwaXJ5QXQiOjE5NjU4LCJlbWFpbCI6ImFqYXpAZ21haWwuY29tIn0.tHg7Bm2tR2JptUc1hl_gP6gNv4QSQGUsL-G4W7wDZsU
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

    public ValidateResponseDto validate(Long userId, String token) throws NotFoundException {
        Optional<Session> sessionOptional = sessionRepository.findByUser_IdAndToken(userId, token);

        if(sessionOptional.isEmpty()){
//            throw new NotFoundException("Session with id = " + userId + " and token = " + token + " does not exist.");

            return new ValidateResponseDto(null, null, null, null, null, SessionStatus.ENDED);

        }

        Session session = sessionOptional.get();
        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
//            throw new NotFoundException("Session's status is ENDED " + SessionStatus.ENDED);
            return new ValidateResponseDto(null, null, null, null, null, SessionStatus.ENDED);
        }

        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256

        System.out.println(key);

        String savedToken = session.getToken();



        Jws<Claims> claimsJws = Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token);
//
        String email = (String)claimsJws.getPayload().get("email");
        List<Role> roles = (List<Role>) claimsJws.getPayload().get("roles");

        Long createdAtt = (Long)claimsJws.getPayload().get("createdAt");

        System.out.println(createdAtt);

//        Date createdAt = (Date) claimsJws.getPayload().get("createdAt");
//        Long expiryAt = (Long) claimsJws.getPayload().get("expiryAt");

        Date createdAt = new Date();
        Date expiryAt = new Date();

        if(createdAt.before(new Date())){
            return new ValidateResponseDto(null, null, null, null, null, SessionStatus.ENDED);
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return new ValidateResponseDto(null, null, null, null, null, SessionStatus.ENDED);
        }

        User user = userOptional.get();

        ValidateResponseDto response = new ValidateResponseDto(
            userId,
                email,
                roles, createdAt,
                expiryAt,
                SessionStatus.ACTIVE
        );


//        System.out.println("i am at line 200");


        return response;

    }
}
