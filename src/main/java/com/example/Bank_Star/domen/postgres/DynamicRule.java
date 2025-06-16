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
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_text")
    private String productText;

    @OneToMany(mappedBy = "dynamicRule", cascade = CascadeType.ALL)
    private List<Rule> rules;
}