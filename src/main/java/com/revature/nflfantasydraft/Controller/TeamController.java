package com.revature.nflfantasydraft.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.nflfantasydraft.Dto.AddPlayerRequestDto;
import com.revature.nflfantasydraft.Dto.TeamRequestDto;
import com.revature.nflfantasydraft.Dto.TeamResponseDto;
import com.revature.nflfantasydraft.Entity.Team;
import com.revature.nflfantasydraft.Service.TeamService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMapper teamMapper;

    
    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;  // Initialize the mapper
    }

    @PostMapping
public ResponseEntity<?> createTeam(@RequestBody TeamRequestDto teamRequestDto, HttpServletRequest request) {
    System.out.println("INSIDE ADD TEAM HERE");
    try {
        System.out.println("STAGE 1");
        Integer userIdFromToken = (Integer) request.getAttribute("userId");

        System.out.println("STAGE 2");
        
        if (userIdFromToken == null) {
            System.out.println("STAGE 2.1");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // if (!userIdFromToken.equals(teamRequestDto.getUserId())) {
        //     System.out.println("STAGE 2.2");
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        // }
        
        System.out.println("Before calling teamService");

        TeamResponseDto responseDto = teamService.createTeam(teamRequestDto);

        System.out.println("After calling teamService");

        return ResponseEntity.ok(responseDto);
    } catch (Exception e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", e.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if it's truly an exception
    }
}

@GetMapping
public ResponseEntity<List<TeamResponseDto>> getUserTeams(HttpServletRequest request) {
    Integer userId = (Integer) request.getAttribute("userId");
    if (userId == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    List<Team> teams = teamService.getTeamsByUserId(userId);
    List<TeamResponseDto> responseDtos = teams.stream()
        .map(this::convertToResponseDto)
        .toList();
    
    return ResponseEntity.ok(responseDtos);
}

private TeamResponseDto convertToResponseDto(Team team) {
    TeamResponseDto dto = new TeamResponseDto();
    dto.setTeamId(team.getTeamId());
    dto.setTeamName(team.getTeamName());
    dto.setUserId(team.getUser().getUserId());
    dto.setUserName(team.getUser().getUserName());
    dto.setIsBot(team.getIsBot());
    if (team.getBot() != null) {
        dto.setBotId(team.getBot().getBotId());
    }
    dto.setLeagueId(team.getLeagueId());
    dto.setQb(team.getQb());
    dto.setRb(team.getRb());
    dto.setWr(team.getWr());
    dto.setTe(team.getTe());
    dto.setK(team.getK());
    return dto;
}

    @GetMapping("/{teamId}")
public ResponseEntity<TeamResponseDto> getTeam(@PathVariable Long teamId, 
                                             HttpServletRequest request) {
    Integer userId = (Integer) request.getAttribute("userId");
    if (userId == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    Team team = teamService.getTeamById(teamId);
    
    // Verify the requesting user owns the team
    // if (!team.getUser().getUserId().equals(userId)) {
    //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    // }
    
    // Convert to DTO
    TeamResponseDto responseDto = convertToResponseDto(team);
    return ResponseEntity.ok(responseDto);
}



    @PutMapping("/{teamId}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long teamId, @RequestBody Team team, 
                                         HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Team existingTeam = teamService.getTeamById(teamId);
        if (!existingTeam.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        team.setTeamId(teamId);
        return ResponseEntity.ok(teamService.updateTeam(team));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Team team = teamService.getTeamById(teamId);
        if (!team.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/add-player")
public ResponseEntity<TeamResponseDto> addPlayerToTeam(
        @PathVariable Long teamId,
        @RequestBody AddPlayerRequestDto addPlayerRequest,
        HttpServletRequest request) {
    
    // Get and validate user
    Integer userId = (Integer) request.getAttribute("userId");
    System.out.println("Authenticated userId: " + userId);
    
    Team team = teamService.getTeamById(teamId);
    System.out.println("Team owner userId: " + team.getUser().getUserId());
    
    if (!team.getUser().getUserId().equals(userId)) {
        System.out.println("Access denied - user doesn't own this team");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Add player and get updated team
    Team updatedTeam = teamService.addPlayerToTeam(
        teamId, 
        addPlayerRequest.getPosition(), 
        addPlayerRequest.getPlayerApiId(), 
        userId
    );

    // Convert to DTO and return
    return ResponseEntity.ok(teamMapper.toResponseDto(updatedTeam));
}

    @GetMapping("/league/{leagueId}")
    public ResponseEntity<List<TeamResponseDto>> getTeamsByLeagueId(@PathVariable Long leagueId) {
        List<TeamResponseDto> teams = teamService.getTeamsByLeagueId(leagueId);
        return ResponseEntity.ok(teams);
    }

}   