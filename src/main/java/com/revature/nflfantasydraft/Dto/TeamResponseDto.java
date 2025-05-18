package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class TeamResponseDto {
    private Long teamId;
    private String teamName;
    private Integer userId;
    private String userName;
    private String qb;
    private String rb;
    private String wr;
    private String te;
    private String k;
    private Boolean isPlayerDrafted;


    // New fields for bot teams
    private Long botId;
    private Long leagueId;
    private String difficultyLevel;
    private String strategy;
    private Boolean isBot;
    private String imgUrl;


    // In TeamResponseDto.java
public String getPlayerNameByPosition(String position) {
    if (position == null) {
        return "Unknown Player";
    }
    
    String playerInfo = null;
    switch (position.toUpperCase()) {
        case "QB": playerInfo = getQb(); break;
        case "RB": playerInfo = getRb(); break;
        case "WR": playerInfo = getWr(); break;
        case "TE": playerInfo = getTe(); break;
        case "K": playerInfo = getK(); break;
        default: return "Unknown Player";
    }
    
    if (playerInfo == null || playerInfo.isEmpty()) {
        return "Unknown Player";
    }
    
    String[] parts = playerInfo.split(",");
    return parts.length > 0 ? parts[0].trim() : "Unknown Player";
}

}  