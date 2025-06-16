package com.example.Bank_Star.domen.postgres;

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

    @ManyToOne
    @JoinColumn(name = "rule_id")  // Теперь это основное поле для связи
    private Rule rule;  // Поле должно называться именно так для mappedBy

    @Column(name = "query_type")
    private String queryType;

    @Column(name = "negate")
    private Boolean negate;

    @Column(name = "arguments", columnDefinition = "jsonb")
    private String arguments;


}