package com.dgnl_backend.project.dgnl_backend.schemas.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "answer", schema = "test")
public class Answer {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "answer_text", nullable = false)
    private String answerText;


    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false)
    private QuestionComponent question;

    public Answer() {}
    public Answer(String answerText, QuestionComponent question) {
        this.answerText = answerText;
        // this.question = question;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getAnswerText() {
        return answerText;
    }


    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
