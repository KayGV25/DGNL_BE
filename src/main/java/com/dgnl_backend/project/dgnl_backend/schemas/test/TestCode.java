package com.dgnl_backend.project.dgnl_backend.schemas.test;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_codes", schema = "test")
public class TestCode {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "testCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> questions;  // Retrieve all questions via Test table

    public List<Question> getQuestions() {
        return questions.stream()
                   .map(Test::getQuestion)
                   .toList();
    }

    public TestCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setQuestions(List<Test> questions) {
        this.questions = questions;
    }
}
