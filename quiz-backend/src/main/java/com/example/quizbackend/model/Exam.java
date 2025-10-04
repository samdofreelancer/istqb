package com.example.quizbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Entity
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private Integer timeLimitSec;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_id")
    @JsonManagedReference
    private Set<Question> questions = new LinkedHashSet<>();
    
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Attempt> attempts = new LinkedHashSet<>();
    
    public void setQuestions(Collection<Question> questions) {
        this.questions.clear();
        if (questions != null) {
            for (Question question : questions) {
                question.setExam(this);
                if (question.getChoices() != null) {
                    question.getChoices().forEach(choice -> choice.setQuestion(question));
                }
            }
            this.questions.addAll(questions);
        }
    }
    
    @PrePersist
    @PreUpdate
    private void prePersist() {
        if (this.questions != null) {
            for (Question question : this.questions) {
                question.setExam(this);
                if (question.getChoices() != null) {
                    question.getChoices().forEach(choice -> choice.setQuestion(question));
                }
            }
        }
    }
}
