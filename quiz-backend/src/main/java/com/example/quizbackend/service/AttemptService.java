package com.example.quizbackend.service;

import com.example.quizbackend.model.*;
import com.example.quizbackend.repository.AttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AttemptService {
    private final AttemptRepository attemptRepository;
    private final ExamService examService;

    @Transactional
    public Attempt submitAttempt(Attempt attempt) {
        // Load the complete exam with questions and choices
        Exam exam = examService.getExamById(attempt.getExam().getId());
        attempt.setExam(exam);
        attempt.setTimestamp(LocalDateTime.now());
        attempt.setTotalQuestions(exam.getQuestions().size());
        
        // Link questions to answers
        for (AttemptAnswer answer : attempt.getAnswers()) {
            for (Question question : exam.getQuestions()) {
                if (question.getId().equals(answer.getQuestion().getId())) {
                    answer.setQuestion(question);
                    answer.setAttempt(attempt);
                    break;
                }
            }
        }
        
        // Calculate score
        int score = calculateScore(attempt);
        attempt.setScore(score);
        
        return attemptRepository.save(attempt);
    }

    private int calculateScore(Attempt attempt) {
        return (int) attempt.getAnswers().stream()
                .filter(this::isAnswerCorrect)
                .count();
    }

    private boolean isAnswerCorrect(AttemptAnswer answer) {
        Question question = answer.getQuestion();
        Set<Long> selectedIds = answer.getSelectedChoiceIds();
        
        if (selectedIds == null || question == null || question.getChoices() == null) {
            return false;
        }
        
        Set<Long> correctIds = question.getChoices().stream()
                .filter(Choice::isCorrect)
                .map(Choice::getId)
                .collect(java.util.stream.Collectors.toSet());
        
        // For multiple choice questions, all correct answers must be selected
        // For single choice questions, exactly one correct answer must be selected
        if (question.isMultiple()) {
            return selectedIds.equals(correctIds);
        } else {
            return selectedIds.size() == 1 && correctIds.containsAll(selectedIds);
        }
    }
}
