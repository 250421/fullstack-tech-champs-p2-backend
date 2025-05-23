package com.revature.nflfantasydraft.Service;

import java.util.List;
import com.revature.nflfantasydraft.Dto.TeamLeaderboardDto;
import com.revature.nflfantasydraft.Dto.TeamRequestDto;
import com.revature.nflfantasydraft.Dto.TeamResponseDto;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Entity.Team;

public interface TeamService {
    TeamResponseDto createTeam(TeamRequestDto teamRequestDto);
    Team updateTeam(Team team);
    void deleteTeam(Long teamId);
    Team getTeamById(Long teamId);
    List<Team> getTeamsByUserId(Integer userId);
    Team addPlayerToTeam(Long teamId, String position, Integer playerApiId, Integer userId);
    TeamResponseDto convertToResponseDto(Team savedTeam);
    List<Player> getPlayersByPositionWithTotalPoints(String position);
    List<TeamResponseDto> getTeamsByLeagueId(Long leagueId);
    List<TeamLeaderboardDto> getTeamsLeaderboard();
}  