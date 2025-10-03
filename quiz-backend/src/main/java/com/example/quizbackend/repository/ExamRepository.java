package com.example.quizbackend.repository;

import com.example.quizbackend.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.questions")
    List<Exam> findAll();
    
    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.questions WHERE e.id = :id")
    Optional<Exam> findById(@Param("id") Long id);
}
