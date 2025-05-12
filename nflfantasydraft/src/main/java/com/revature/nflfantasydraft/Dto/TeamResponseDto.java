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
}