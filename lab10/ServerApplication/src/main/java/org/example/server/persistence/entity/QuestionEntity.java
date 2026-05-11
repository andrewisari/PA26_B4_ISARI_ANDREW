package org.example.server.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, length = 32)
    private String externalId;

    @Column(name = "text", nullable = false, length = 512)
    private String text;

    @Column(name = "option_a", nullable = false, length = 256)
    private String optionA;

    @Column(name = "option_b", nullable = false, length = 256)
    private String optionB;

    @Column(name = "option_c", nullable = false, length = 256)
    private String optionC;

    @Column(name = "option_d", nullable = false, length = 256)
    private String optionD;

    @Column(name = "correct_index", nullable = false)
    private int correctIndex;

    public QuestionEntity(String externalId, String text,
                          String optionA, String optionB, String optionC, String optionD,
                          int correctIndex) {
        this.externalId = externalId;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctIndex = correctIndex;
    }

    public List<String> options() {
        return List.of(optionA, optionB, optionC, optionD);
    }
}
