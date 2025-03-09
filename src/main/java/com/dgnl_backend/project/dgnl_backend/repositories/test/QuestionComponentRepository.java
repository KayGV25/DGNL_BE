package com.dgnl_backend.project.dgnl_backend.repositories.test;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dgnl_backend.project.dgnl_backend.dtos.test.CorrectAnswerWithQuestionIdDTO;
import com.dgnl_backend.project.dgnl_backend.schemas.test.QuestionComponent;

@Repository
public interface QuestionComponentRepository extends JpaRepository<QuestionComponent, Long> {
    
    @Query("SELECT new com.dgnl_backend.project.dgnl_backend.dto.QuestionCorrectAnswerDTO(q.id, q.correctAnswer) FROM QuestionComponent q WHERE q.test.id = :testId")
    List<CorrectAnswerWithQuestionIdDTO> findCorrectAnswersByTestId(Long testId);
}
