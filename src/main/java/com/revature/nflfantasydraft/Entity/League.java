package com.revature.nflfantasydraft.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "leagues")
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "num_players", nullable = false)
    private Integer numPlayers;

    @Column(name = "drafting", nullable = false)
    private Boolean drafting = true;

    @Column(name = "current_pick", nullable = false)
    private Integer currentPick = 1;

    public League() {}

    public League(Integer id, Integer numPlayers, Boolean drafting, Integer currentPick) {
        this.id = id;
        this.numPlayers = numPlayers;
        this.drafting = drafting;
        this.currentPick = currentPick;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getNumPlayers() {
        return numPlayers;
    }

    public Boolean getDrafting() {
        return drafting;
    }

    public Integer getCurrentPick() {
        return currentPick;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumPlayers(Integer numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void setDrafting(Boolean drafting) {
        this.drafting = drafting;
    }

    public void setCurrentPick(Integer currentPick) {
        this.currentPick = currentPick;
    }
}
