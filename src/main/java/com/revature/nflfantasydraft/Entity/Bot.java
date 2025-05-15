package com.revature.nflfantasydraft.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bots")
@Data
public class Bot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long botId;

    @Column(name = "league_id")
    private Long leagueId;

    @Column(name = "team_id", insertable = false, updatable = false)
    private Long teamId;

    @Column(name = "difficulty_level")
    private String difficultyLevel; // e.g., "EASY", "MEDIUM", "HARD"

    @Column(name = "strategy")
    private String strategy; // e.g., "BALANCED", "OFFENSIVE", "DEFENSIVE"

    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;
}    