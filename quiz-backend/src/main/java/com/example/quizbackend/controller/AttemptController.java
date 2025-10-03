package com.example.quizbackend.controller;

import com.example.quizbackend.model.Attempt;
import com.example.quizbackend.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AttemptController {
    private final AttemptService attemptService;

    @PostMapping(
        value = "/submit",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> submitAttempt(@RequestBody Attempt attempt) {
        try {
            Attempt savedAttempt = attemptService.submitAttempt(attempt);
            return ResponseEntity.ok(Map.of(
                "score", savedAttempt.getScore(),
                "totalQuestions", savedAttempt.getTotalQuestions()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to submit attempt",
                "message", e.getMessage()
            ));
        }
    }
}
