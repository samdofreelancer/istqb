package com.example.quizbackend.service;

import com.example.quizbackend.model.*;
import com.example.quizbackend.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;

    @Transactional
    public Exam importExam(Exam exam) {
        // Check if an exam with this title already exists
        Optional<Exam> existingExam = examRepository.findByTitle(exam.getTitle());
        
        if (existingExam.isPresent()) {
            // Update existing exam
            Exam toUpdate = existingExam.get();
            toUpdate.setDescription(exam.getDescription());
            toUpdate.setTimeLimitSec(exam.getTimeLimitSec());
            
            // Clear and update questions
            Set<Question> questions = toUpdate.getQuestions();
            questions.clear(); // This will trigger cascade delete due to orphanRemoval=true
            
            // Add new questions
            exam.getQuestions().forEach(q -> {
                q.setExam(toUpdate);
                q.getChoices().forEach(c -> c.setQuestion(q));
                questions.add(q);
            });
            
            return examRepository.save(toUpdate);
        }
        
        // If no existing exam found, create new one
        exam.getQuestions().forEach(q -> {
            q.setExam(exam);
            q.getChoices().forEach(c -> c.setQuestion(q));
        });
        return examRepository.save(exam);
    }

    @Transactional(readOnly = true)
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Exam getExamById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
    }
}
