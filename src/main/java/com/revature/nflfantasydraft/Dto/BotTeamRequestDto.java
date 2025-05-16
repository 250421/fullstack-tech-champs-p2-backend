package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class BotTeamRequestDto {
    private Long botId;
    private String teamName;
    private Long leagueId;
}    