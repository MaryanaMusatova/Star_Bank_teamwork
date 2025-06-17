package com.example.Bank_Star.domen.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "query", schema = "public")
@Data
public class RuleQuery {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    @JsonIgnore
    private Rule rule;

    @Column(name = "query_type")
    private String queryType;

    @Column(name = "negate")
    private Boolean negate;

    @Column(name = "arguments", columnDefinition = "jsonb")
    private String arguments;
}