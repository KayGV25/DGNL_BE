package com.dgnl_backend.project.dgnl_backend.services.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dgnl_backend.project.dgnl_backend.dtos.test.CorrectAnswerWithQuestionIdDTO;
import com.dgnl_backend.project.dgnl_backend.repositories.test.QuestionComponentRepository;

@Service
public class QuestionService {

    @Autowired
    private QuestionComponentRepository questionComponentRepository;

    public List<CorrectAnswerWithQuestionIdDTO> getCorrectAnswersForTest(Long testId) {
        return questionComponentRepository.findCorrectAnswersByTestId(testId);
    }
}
