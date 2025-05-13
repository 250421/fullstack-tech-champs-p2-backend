package com.revature.nflfantasydraft.Entity;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "players")
@Data
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @Column(name = "team_id")
    private Long teamId; // This will be set separately as a foreign key

    @Column(name = "player_api_id")  // Add this column
    private Integer playerApiId;  // This will store the ID from the API


    private Integer season;
    private Integer week;
    private String team;
    private String opponent;
    private Integer number;
    private String name;
    private String position;
    private Double fantasyPoints;

    public Player() {} 

    public Player(Integer playerApiId, String name, String team, String position, Double fantasyPoints) {
        this.playerApiId = playerApiId;
        this.name = name;
        this.team = team;
        this.position = position;
        this.fantasyPoints = fantasyPoints;
    }



}


