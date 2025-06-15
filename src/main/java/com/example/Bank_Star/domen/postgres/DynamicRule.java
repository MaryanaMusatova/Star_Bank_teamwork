package com.example.Bank_Star.domen.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rules", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String productName;
    private UUID productId;
    private String productText;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "dynamic_rule_id")
    private List<RuleQuery> rule;
}