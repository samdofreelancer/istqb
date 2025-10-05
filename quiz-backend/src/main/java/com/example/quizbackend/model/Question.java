package com.example.quizbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "text")
    private String text;
    
    private boolean multiple;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Exam exam;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    @OrderColumn(name = "choice_order")
    @JsonManagedReference
    private List<Choice> choices = new ArrayList<>();
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AttemptAnswer> attemptAnswers = new LinkedHashSet<>();
    
    public void setChoices(List<Choice> choices) {
        this.choices.clear();
        if (choices != null) {
            choices.forEach(choice -> choice.setQuestion(this));
            this.choices.addAll(choices);
        }
    }
}
