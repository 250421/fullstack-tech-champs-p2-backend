package com.revature.nflfantasydraft.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "teams")
@Data
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "qb")
    private String qb; // Format: "Name,Team,FantasyPoints"

    @Column(name = "rb")
    private String rb; // Format: "Name,Team,FantasyPoints"

    @Column(name = "wr")
    private String wr; // Format: "Name,Team,FantasyPoints"

    @Column(name = "te")
    private String te; // Format: "Name,Team,FantasyPoints"

    @Column(name = "k")
    private String k; // Format: "Name,Team,FantasyPoints"

    // New fields for bot functionality

    @Column(name = "is_bot")
    private Boolean isBot = false;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(name = "league_id")
    private Long leagueId;

}  