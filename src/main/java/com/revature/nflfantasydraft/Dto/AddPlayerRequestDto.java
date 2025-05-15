package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class AddPlayerRequestDto {
    private String position; // "QB", "RB", "WR", "TE", "K"
    private Integer playerApiId; // Changed from playerInfo to playerApiId
}   