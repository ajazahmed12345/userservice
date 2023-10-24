package com.ajaz.userservice.repositories;

import com.ajaz.userservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByIdIn(List<Long> longs);
}
