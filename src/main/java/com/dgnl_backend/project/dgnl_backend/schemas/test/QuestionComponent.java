package com.dgnl_backend.project.dgnl_backend.schemas.test;

import java.util.ArrayList;
import java.util.List;

import com.dgnl_backend.project.dgnl_backend.utils.Pair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_components", schema = "test")
public class QuestionComponent {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "answer_one", nullable = false)
    @JsonIgnore
    private String answerOne;
    
    @Column(name = "answer_two", nullable = false)
    @JsonIgnore
    private String answerTwo;
    
    @Column(name = "answer_three", nullable = false)
    @JsonIgnore
    private String answerThree;
    
    @Column(name = "answer_four", nullable = false)
    @JsonIgnore
    private String answerFour;

    @Column(name = "correct_answer", nullable = false)
    @JsonIgnore
    private Integer correctAnswer;
    
    @JsonProperty("answers_list")
    public List<Pair<Integer, String>> getAnswersList(){
        List<Pair<Integer, String>> answers = new ArrayList<>();
        answers.add(new Pair<>(1, answerOne));
        answers.add(new Pair<>(2, answerTwo));
        answers.add(new Pair<>(3, answerThree));
        answers.add(new Pair<>(4, answerFour));
        return answers;
    }
    
    public QuestionComponent() {}
    public QuestionComponent(String questionText, List<String> answers) {
        this.questionText = questionText;
        this.answerOne = answers.get(0);
        this.answerTwo = answers.get(1);
        this.answerThree = answers.get(2);
        this.answerFour = answers.get(3);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getAnswerOne() {
        return answerOne;
    }
    public void setAnswerOne(String answerOne) {
        this.answerOne = answerOne;
    }
    public String getAnswerTwo() {
        return answerTwo;
    }
    public void setAnswerTwo(String answerTwo) {
        this.answerTwo = answerTwo;
    }
    public String getAnswerThree() {
        return answerThree;
    }
    public void setAnswerThree(String answerThree) {
        this.answerThree = answerThree;
    }
    public String getAnswerFour() {
        return answerFour;
    }
    public void setAnswerFour(String answerFour) {
        this.answerFour = answerFour;
    }
    
    public Integer getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false)
    private Question question;
}
