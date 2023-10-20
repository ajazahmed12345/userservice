package com.ajaz.userservice.repositories;

import com.ajaz.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

//    @Query(value = "select * from session where user_id=:userId AND token=:token", nativeQuery = true)
    Optional<Session> findByUser_IdAndToken(Long userId, String token);
}
