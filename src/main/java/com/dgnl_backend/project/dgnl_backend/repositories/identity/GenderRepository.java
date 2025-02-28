package com.dgnl_backend.project.dgnl_backend.repositories.identity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dgnl_backend.project.dgnl_backend.schemas.identity.Gender;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Integer> {

}
