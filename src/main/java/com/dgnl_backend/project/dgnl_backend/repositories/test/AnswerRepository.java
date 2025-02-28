package com.dgnl_backend.project.dgnl_backend.repositories.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dgnl_backend.project.dgnl_backend.schemas.test.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
