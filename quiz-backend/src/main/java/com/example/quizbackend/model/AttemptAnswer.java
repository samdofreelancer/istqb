package com.example.quizbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class AttemptAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Attempt attempt;
    
    @ManyToOne
    private Question question;
    
    @ElementCollection
    private Set<Long> selectedChoiceIds = new HashSet<>();
}
