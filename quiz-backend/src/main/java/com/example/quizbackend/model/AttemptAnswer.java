package com.example.quizbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class AttemptAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JsonIgnore
    private Attempt attempt;
    
    @ManyToOne
    private Question question;
    
    @ElementCollection
    @CollectionTable(
        name = "attempt_answer_choices",
        joinColumns = @JoinColumn(name = "attempt_answer_id")
    )
    @Column(name = "choice_id")
    private Set<Long> selectedChoiceIds = new HashSet<>();
    
    public void addSelectedChoice(Long choiceId) {
        if (this.selectedChoiceIds == null) {
            this.selectedChoiceIds = new HashSet<>();
        }
        this.selectedChoiceIds.add(choiceId);
    }
}
