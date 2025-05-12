package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class AddPlayerRequestDto {
    private String position; // "QB", "RB", "WR", "TE", "K"
    private String playerInfo; // "PlayerName,Team,Points" format
}