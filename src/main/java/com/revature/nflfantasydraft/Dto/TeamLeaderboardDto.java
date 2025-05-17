package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class TeamLeaderboardDto {
    private String teamName;
    private String imgUrl;
    private Double totalFantasyPoints;
}