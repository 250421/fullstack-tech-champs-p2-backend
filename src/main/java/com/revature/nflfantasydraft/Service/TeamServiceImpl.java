package com.revature.nflfantasydraft.Service;

import com.revature.nflfantasydraft.Dto.TeamLeaderboardDto;
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
import java.util.Map;
import java.util.stream.Collectors;

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
    System.out.println("INSIDE TEAM SERVICE");
    System.out.println(teamRequestDto.getTeamName());
    // Validate user exists
    User user = userRepository.findById(teamRequestDto.getUserId())
        .orElseThrow(() -> new ETeamException("User not found"));

    System.out.println("About to get fields");
    
    // Create and save team
    Team team = new Team();
    team.setTeamName(teamRequestDto.getTeamName());
    team.setUser(user);
    team.setLeagueId(teamRequestDto.getLeagueId());
    team.setQb(teamRequestDto.getQb());
    team.setRb(teamRequestDto.getRb());
    team.setWr(teamRequestDto.getWr());
    team.setTe(teamRequestDto.getTe());
    team.setK(teamRequestDto.getK());

    System.out.println("About to save");

    System.out.println(team);
    
    Team savedTeam = teamRepository.save(team);
    
    // Convert to response DTO
    return convertToResponseDto(savedTeam);
}

@Override
public TeamResponseDto convertToResponseDto(Team team) {
    if (team == null) {
        return null;
    }

    TeamResponseDto responseDto = new TeamResponseDto();
    responseDto.setTeamId(team.getTeamId());
    responseDto.setTeamName(team.getTeamName());
    responseDto.setQb(team.getQb());
    responseDto.setRb(team.getRb());
    responseDto.setWr(team.getWr());
    responseDto.setTe(team.getTe());
    responseDto.setK(team.getK());
    responseDto.setIsBot(team.getIsBot() != null ? team.getIsBot() : false);
    responseDto.setImgUrl(team.getImgUrl());
    responseDto.setLeagueId(team.getLeagueId());

    // Handle User data
    if (team.getUser() != null) {
        responseDto.setUserId(team.getUser().getUserId());
        responseDto.setUserName(team.getUser().getUserName());
    } else {
        // Default values for bot teams
        responseDto.setUserId(-1);
        responseDto.setUserName("System Bot");
    }

    // Handle Bot data
    if (team.getBot() != null) {
        responseDto.setBotId(team.getBot().getBotId());
        responseDto.setDifficultyLevel(team.getBot().getDifficultyLevel());
        responseDto.setStrategy(team.getBot().getStrategy());
    }

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
    
    // Mark all player records as drafted
    players.forEach(player -> {
        player.setIsDrafted(true);
        playerRepository.save(player);
    });

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
    String playerInfo = String.format("%s, %s, %s, %.1f", 
        player.getName(), 
        player.getTeam(), 
        player.getPlayerApiId(),
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

@Override
public List<Player> getPlayersByPositionWithTotalPoints(String position) {
    // Get all available (undrafted) players for the position
    List<Player> availablePlayers = playerRepository.findAvailablePlayersByPosition(position);
    
    // Create a custom class to use as grouping key
    record PlayerKey(Integer playerApiId, String name, String team, String position) {}
    
    // Group and sum fantasy points
    Map<PlayerKey, Double> playerPoints = availablePlayers.stream()
        .collect(Collectors.groupingBy(
            player -> new PlayerKey(
                player.getPlayerApiId(),
                player.getName(),
                player.getTeam(),
                player.getPosition()
            ),
            Collectors.summingDouble(Player::getFantasyPoints)
        ));
    
    // Convert to list of Player objects
    return playerPoints.entrySet().stream()
        .map(entry -> {
            Player player = new Player();
            player.setPlayerApiId(entry.getKey().playerApiId());
            player.setName(entry.getKey().name());
            player.setTeam(entry.getKey().team());
            player.setPosition(entry.getKey().position());
            player.setFantasyPoints(entry.getValue());
            return player;
        })
        .sorted((p1, p2) -> Double.compare(p2.getFantasyPoints(), p1.getFantasyPoints()))
        .collect(Collectors.toList());
}

        @Override
        public List<TeamResponseDto> getTeamsByLeagueId(Long leagueId) {
            List<Team> teams = teamRepository.findByLeagueId(leagueId);
            return teams.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
        }

        @Override
public List<TeamLeaderboardDto> getTeamsLeaderboard() {
    List<Team> allTeams = teamRepository.findAll();
    
    return allTeams.stream()
        .map(team -> {
            TeamLeaderboardDto dto = new TeamLeaderboardDto();
            dto.setTeamName(team.getTeamName());
            dto.setImgUrl(team.getImgUrl());
            dto.setTotalFantasyPoints(calculateTotalFantasyPoints(team));
            return dto;
        })
        .sorted((t1, t2) -> Double.compare(t2.getTotalFantasyPoints(), t1.getTotalFantasyPoints()))
        .collect(Collectors.toList());
}

private Double calculateTotalFantasyPoints(Team team) {
    double total = 0.0;
    
    // Helper method to parse player info and extract points
    total += parseFantasyPoints(team.getQb());
    total += parseFantasyPoints(team.getRb());
    total += parseFantasyPoints(team.getWr());
    total += parseFantasyPoints(team.getTe());
    total += parseFantasyPoints(team.getK());
    
    return total;
}

private Double parseFantasyPoints(String playerInfo) {
    if (playerInfo == null || playerInfo.isEmpty()) {
        return 0.0;
    }
    
    try {
        // Assuming format is "Name,Team,PlayerApiId,FantasyPoints"
        String[] parts = playerInfo.split(",");
        if (parts.length >= 4) {
            return Double.parseDouble(parts[3].trim());
        }
    } catch (Exception e) {
        // Log error if needed
    }
    return 0.0;
}

}  