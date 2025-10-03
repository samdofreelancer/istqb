package com.example.quizbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Exam exam;
    
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime timestamp;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "attempt_id")
    private List<AttemptAnswer> answers = new ArrayList<>();
    
    public void setAnswers(List<AttemptAnswer> answers) {
        this.answers.clear();
        if (answers != null) {
            answers.forEach(answer -> answer.setAttempt(this));
            this.answers.addAll(answers);
        }
    }
}
