package com.example.quizbackend.controller;

import com.example.quizbackend.model.Attempt;
import com.example.quizbackend.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class AttemptController {
    private final AttemptService attemptService;

    @PostMapping("/submit")
    public ResponseEntity<Attempt> submitAttempt(@RequestBody Attempt attempt) {
        return ResponseEntity.ok(attemptService.submitAttempt(attempt));
    }
}
