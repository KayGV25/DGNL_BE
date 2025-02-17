package com.dgnl_backend.project.dgnl_backend.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dgnl_backend.project.dgnl_backend.schemas.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByToken(String token);
    Token findByUserId(UUID userId);
    boolean existsByToken(String token);
    boolean existsByUserId(UUID userId);
    void deleteByToken(String token);
}
