package org.example.server.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class GameEntity {

    public enum Outcome { P1_WIN, P2_WIN, DRAW, P1_ABANDONED, P2_ABANDONED, BOTH_ABANDONED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player1_id", nullable = false)
    private PlayerEntity player1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player2_id", nullable = false)
    private PlayerEntity player2;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "questions_total", nullable = false)
    private int questionsTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", length = 24)
    private Outcome outcome;
}
