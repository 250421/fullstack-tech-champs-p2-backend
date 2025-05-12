package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class TeamRequestDto {
    private String teamName;
    private Integer userId; // Just need the ID, not the whole user object
    private String qb;
    private String rb;
    private String wr;
    private String te;
    private String k;
}
