package com.example.Bank_Star.controller;

import com.example.Bank_Star.domen.RecommendationResponse;
import com.example.Bank_Star.repository.RecommendationsRepository;
import com.example.Bank_Star.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/userId")
    public RecommendationResponse getRecommendations(@PathVariable UUID userId) {
        return service.getRecommendations(userId);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
    }
}