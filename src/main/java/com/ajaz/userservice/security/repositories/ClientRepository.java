package com.ajaz.userservice.security.repositories;

import java.util.Optional;

import com.ajaz.userservice.security.models.Client;
//import sample.jpa.entity.client.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientId(String clientId);
}


