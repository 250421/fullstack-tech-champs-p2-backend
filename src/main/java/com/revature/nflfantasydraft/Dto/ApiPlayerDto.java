package com.revature.nflfantasydraft.Dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiPlayerDto {
    
    @JsonProperty("PlayerID")  // This matches the JSON field name
    private Integer playerApiId;  // We'll use this for duplicate checking
    
    
    @JsonProperty("Season")
    private Integer season;
    
    @JsonProperty("Week")
    private Integer week;
    
    @JsonProperty("Team")
    private String team;
    
    @JsonProperty("Opponent")
    private String opponent;
    
    @JsonProperty("Number")
    private Integer number;
    
    @JsonProperty("Name")
    private String name;
    
    @JsonProperty("Position")
    private String position;
    
    @JsonProperty("FantasyPoints")
    private Double fantasyPoints;
    

}


   