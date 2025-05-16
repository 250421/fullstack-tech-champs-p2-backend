package com.revature.nflfantasydraft.Dto;

import lombok.Data;

@Data
public class BotPickPlayerRequestDto {
    private Long botId;
    private Long teamId;
    private String position;
}    

