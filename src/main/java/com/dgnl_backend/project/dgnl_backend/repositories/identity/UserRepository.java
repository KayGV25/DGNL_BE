package com.dgnl_backend.project.dgnl_backend.repositories.identity;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dgnl_backend.project.dgnl_backend.schemas.identity.User;


@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByEmail(String email);
}
