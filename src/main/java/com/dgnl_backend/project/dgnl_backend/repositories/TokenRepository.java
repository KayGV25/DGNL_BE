package com.dgnl_backend.project.dgnl_backend.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dgnl_backend.project.dgnl_backend.schemas.identity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUserId(UUID userId);
    boolean existsByToken(String token);
    boolean existsByUserId(UUID userId);
    void deleteByToken(String token);
}
