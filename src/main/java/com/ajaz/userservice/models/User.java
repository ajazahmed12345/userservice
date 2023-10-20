package com.ajaz.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class User extends BaseModel{

    private String email;
    private String password;

    @ManyToMany
    private Set<Role> roles = new HashSet<>();

}
