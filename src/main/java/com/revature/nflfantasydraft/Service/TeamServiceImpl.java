package com.revature.nflfantasydraft.Service;

import com.revature.nflfantasydraft.Dto.TeamRequestDto;
import com.revature.nflfantasydraft.Dto.TeamResponseDto;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Entity.Team;
import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.ETeamException;
import com.revature.nflfantasydraft.Repository.PlayerRepository;
import com.revature.nflfantasydraft.Repository.TeamRepository;
import com.revature.nflfantasydraft.Repository.UserRepository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired // Add this annotation
    private PlayerRepository playerRepository; 

    
    @Override
public TeamResponseDto createTeam(TeamRequestDto teamRequestDto) {
    // Validate user exists
    User user = userRepository.findById(teamRequestDto.getUserId())
        .orElseThrow(() -> new ETeamException("User not found"));
    
    // Create and save team
    Team team = new Team();
    team.setTeamName(teamRequestDto.getTeamName());
    team.setUser(user);
    team.setQb(teamRequestDto.getQb());
    team.setRb(teamRequestDto.getRb());
    team.setWr(teamRequestDto.getWr());
    team.setTe(teamRequestDto.getTe());
    team.setK(teamRequestDto.getK());
    
    Team savedTeam = teamRepository.save(team);
    
    // Convert to response DTO
    return convertToResponseDto(savedTeam);
}

private TeamResponseDto convertToResponseDto(Team team) {
    TeamResponseDto responseDto = new TeamResponseDto();
    responseDto.setTeamId(team.getTeamId());
    responseDto.setTeamName(team.getTeamName());
    responseDto.setUserId(team.getUser().getUserId());
    responseDto.setUserName(team.getUser().getUserName());
    responseDto.setQb(team.getQb());
    responseDto.setRb(team.getRb());
    responseDto.setWr(team.getWr());
    responseDto.setTe(team.getTe());
    responseDto.setK(team.getK());
    return responseDto;
}

    @Override
    public Team updateTeam(Team team) {
        if (!teamRepository.existsById(team.getTeamId())) {
            throw new ETeamException("Team not found");
        }
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ETeamException("Team not found");
        }
        teamRepository.deleteById(teamId);
    }

    @Override
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
            .orElseThrow(() -> new ETeamException("Team not found"));
    }

    @Override
    public List<Team> getTeamsByUserId(Integer userId) {
        return teamRepository.findByUserUserId(userId);
    }

    @Override
public Team addPlayerToTeam(Long teamId, String position, Integer playerApiId, Integer userId) {
    // Get team and verify ownership
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new ETeamException("Team not found"));
    
    if (!team.getUser().getUserId().equals(userId)) {
        throw new ETeamException("Unauthorized to modify this team");
    }

    // 1. Get all player records for this API ID
    List<Player> players = playerRepository.findAllByPlayerApiId(playerApiId);
    if (players.isEmpty()) {
        throw new ETeamException("Player not found with ID: " + playerApiId);
    }
    
    // Use the first player (they should all have the same name/team/position)
    Player player = players.get(0);

    // 2. Calculate total points across all records
    Double totalPoints = players.stream()
        .mapToDouble(Player::getFantasyPoints)
        .sum();

    // 3. Verify position matches
    if (!player.getPosition().equalsIgnoreCase(position)) {
        throw new ETeamException("Player position (" + player.getPosition() + 
                               ") doesn't match requested position (" + position + ")");
    }

    // 4. Format the player info string
    String playerInfo = String.format("%s,%s,%.1f", 
        player.getName(), 
        player.getTeam(), 
        totalPoints);

    // Update the appropriate position
    switch (position.toUpperCase()) {
        case "QB":
            team.setQb(playerInfo);
            break;
        case "RB":
            team.setRb(playerInfo);
            break;
        case "WR":
            team.setWr(playerInfo);
            break;
        case "TE":
            team.setTe(playerInfo);
            break;
        case "K":
            team.setK(playerInfo);
            break;
        default:
            throw new ETeamException("Invalid position: " + position);
    }

    return teamRepository.save(team);
}
}