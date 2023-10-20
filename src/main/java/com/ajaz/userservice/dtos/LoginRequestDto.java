package com.ajaz.userservice.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginRequestDto {
    private String email;
    private String password;
}
