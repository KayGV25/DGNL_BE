package com.dgnl_backend.project.dgnl_backend.schemas.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tests", schema = "test")
public class Test {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    public Test(TestCode testCode, Question question) {
        this.testCode = testCode;
        this.question = question;
    }

    @ManyToOne
    @JoinColumn(name = "test_code", referencedColumnName = "id", nullable = false)
    private TestCode testCode;  // Foreign key reference to TestCode

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false)
    private Question question;  // Foreign key reference to Question

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TestCode getTestCode() {
        return testCode;
    }

    public void setTestCode(TestCode testCode) {
        this.testCode = testCode;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
