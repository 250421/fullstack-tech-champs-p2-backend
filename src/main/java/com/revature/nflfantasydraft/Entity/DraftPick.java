package com.revature.nflfantasydraft.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "draft_picks")
public class DraftPick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "league_id", nullable = false)
    private Integer leagueId;

    @Column(name = "pick_number", nullable = false)
    private Integer pickNumber;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Column(name = "player_data")
    private String playerData;

    public DraftPick() {}

    public DraftPick(Integer id, Integer leagueId, Integer pickNumber, Integer teamId, String playerData) {
        this.id = id;
        this.leagueId = leagueId;
        this.pickNumber = pickNumber;
        this.teamId = teamId;
        this.playerData = playerData;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public Integer getPickNumber() {
        return pickNumber;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public String getPlayerData() {
        return playerData;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public void setPickNumber(Integer pickNumber) {
        this.pickNumber = pickNumber;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public void setPlayerData(String playerData) {
        this.playerData = playerData;
    }
}
