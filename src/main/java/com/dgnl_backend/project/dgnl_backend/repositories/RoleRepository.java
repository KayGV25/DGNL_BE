package com.dgnl_backend.project.dgnl_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dgnl_backend.project.dgnl_backend.schemas.identity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {


}
