package com.example.Bank_Star.controller;

import com.example.Bank_Star.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class ManagementController {

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/clear-caches")
    public void clearCaches() {
        recommendationService.clearCaches();
    }
}