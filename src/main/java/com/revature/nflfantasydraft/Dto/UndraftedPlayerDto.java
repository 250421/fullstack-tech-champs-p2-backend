package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class UndraftedPlayerDto {
    private Integer playerApiId;
    private String name;
    private String position;
    private String team;
    private Double fantasyPoints;
    private Boolean drafted;

    // Add this exact constructor
    public UndraftedPlayerDto(Integer playerApiId, String name, String team, 
                        String position, Double fantasyPoints, Boolean isDrafted) {
    this.playerApiId = playerApiId;
    this.name = name;
    this.team = team;
    this.position = position;
    this.fantasyPoints = fantasyPoints;
    this.drafted = isDrafted;
}
    
    // Keep default constructor if needed for Jackson
    public UndraftedPlayerDto() {}
}