package com.example.Bank_Star.domen.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "query")
public class RuleQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "query_type")  // Соответствует столбцу в БД
    private String queryType;

    @Column(name = "arguments")
    private String arguments;

    @Column(name = "negate")
    private Boolean negate;

    @Column(name = "rule_id")
    private Long ruleId;
}