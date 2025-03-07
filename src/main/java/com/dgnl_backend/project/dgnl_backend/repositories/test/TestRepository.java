package com.dgnl_backend.project.dgnl_backend.repositories.test;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dgnl_backend.project.dgnl_backend.schemas.test.Test;

public interface TestRepository extends JpaRepository<Test, Long> {

}
