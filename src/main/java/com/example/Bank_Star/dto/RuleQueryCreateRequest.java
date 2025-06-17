package com.example.Bank_Star.dto;

import lombok.Data;

@Data
public class RuleQueryCreateRequest {
    private String queryType;
    private Boolean negate;
    private String arguments;
}