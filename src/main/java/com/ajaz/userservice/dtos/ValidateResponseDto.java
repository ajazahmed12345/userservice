package com.ajaz.userservice.dtos;

import com.ajaz.userservice.models.Role;
import com.ajaz.userservice.models.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidateResponseDto {
    private Long userId;
    private String email;
    private List<Role> roles = new ArrayList<>();
    private Date createdAt;
    private Date expiryAt;
    private SessionStatus sessionStatus;
}
