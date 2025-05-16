package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class BotRequestDto {
    private Long leagueId;
    private Long teamId;
    private String difficultyLevel;
    private String strategy;
}    