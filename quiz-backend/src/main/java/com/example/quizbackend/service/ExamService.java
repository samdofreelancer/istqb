package com.example.quizbackend.service;

import com.example.quizbackend.model.*;
import com.example.quizbackend.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;

    @Transactional
    public Exam importExam(Exam exam) {
        exam.getQuestions().forEach(question -> {
            question.setExam(exam);
            question.getChoices().forEach(choice -> choice.setQuestion(question));
        });
        return examRepository.save(exam);
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam getExamById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
    }
}
