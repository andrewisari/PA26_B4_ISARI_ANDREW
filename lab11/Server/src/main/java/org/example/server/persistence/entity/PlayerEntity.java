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

import java.time.Instant;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 64)
    private String name;

    @Column(name = "games_played", nullable = false)
    private int gamesPlayed;

    @Column(name = "total_correct", nullable = false)
    private int totalCorrect;

    @Column(name = "wins", nullable = false)
    private int wins;

    @Column(name = "losses", nullable = false)
    private int losses;

    @Column(name = "draws", nullable = false)
    private int draws;

    @Column(name = "last_seen", nullable = false)
    private Instant lastSeen;

    public PlayerEntity(String name) {
        this.name = name;
        this.lastSeen = Instant.now();
    }
}
