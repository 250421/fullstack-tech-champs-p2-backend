package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class TeamDto {
    private String teamName;
    private Integer userId;
    private String qb;
    private String rb;
    private String wr;
    private String te;
    private String k;
}