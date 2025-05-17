package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class UndraftedPlayerDto {
    private Integer playerApiId;
    private String name;
    private String position;
    private Double fantasyPoints;
    private Boolean drafted;
}