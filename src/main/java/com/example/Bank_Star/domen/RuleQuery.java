package com.example.Bank_Star.domen;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rule_queries")
public class RuleQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String query;
    private String arguments;
    private Boolean negate;
}