package com.revature.nflfantasydraft.Controller;

import org.springframework.stereotype.Component;

import com.revature.nflfantasydraft.Dto.TeamResponseDto;
import com.revature.nflfantasydraft.Entity.Team;

@Component  
public class TeamMapper {
    public TeamResponseDto toResponseDto(Team team) {
        TeamResponseDto dto = new TeamResponseDto();
        dto.setTeamId(team.getTeamId());
        dto.setTeamName(team.getTeamName());
        dto.setUserId(team.getUser().getUserId());
        dto.setUserName(team.getUser().getUserName());
        dto.setQb(team.getQb());
        dto.setRb(team.getRb());
        dto.setWr(team.getWr());
        dto.setTe(team.getTe());
        dto.setK(team.getK());
        return dto;
    }
}   