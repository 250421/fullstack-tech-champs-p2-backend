// BotPickResponseDto.java
package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class BotPickResponseDto {
    private TeamResponseDto team;
    private String pickedPlayerName;
    private String pickedPlayerTeam;
    private String pickedPosition;
    
}