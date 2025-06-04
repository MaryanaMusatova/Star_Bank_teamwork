package com.example.Bank_Star.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class InfoController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${project.version}")
    private String version;

    @GetMapping("/info")
    public String getInfo() {
        return "{" +
                "\"name\": \"" + applicationName + "\"," +
                "\"version\": \"" + version + "\"" +
                "}";
    }
}