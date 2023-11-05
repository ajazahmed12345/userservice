package com.ajaz.userservice.security;

import com.ajaz.userservice.models.Role;
import com.ajaz.userservice.models.User;
import com.ajaz.userservice.security.services.CustomSpringGrantedAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Data
@JsonDeserialize(as = CustomSpringUserDetails.class)
public class CustomSpringUserDetails implements UserDetails, Serializable {

    private User user;

    public CustomSpringUserDetails(){

    }

    public CustomSpringUserDetails(User user){
        this.user = user;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<CustomSpringGrantedAuthority> customSpringGrantedAuthorities = new ArrayList<>();

        for(Role role : user.getRoles()){
            customSpringGrantedAuthorities.add(
                    new CustomSpringGrantedAuthority(role)
            );
        }


        return customSpringGrantedAuthorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
