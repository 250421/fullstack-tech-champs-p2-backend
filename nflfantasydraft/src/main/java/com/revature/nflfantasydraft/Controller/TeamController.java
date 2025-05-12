package com.revature.nflfantasydraft.Controller;

import com.revature.nflfantasydraft.Dto.AddPlayerRequestDto;
import com.revature.nflfantasydraft.Dto.TeamRequestDto;
import com.revature.nflfantasydraft.Dto.TeamResponseDto;
import com.revature.nflfantasydraft.Entity.Team;
import com.revature.nflfantasydraft.Service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public ResponseEntity<TeamResponseDto> createTeam(@RequestBody TeamRequestDto teamRequestDto, 
                                               HttpServletRequest request) {
    Integer userIdFromToken = (Integer) request.getAttribute("userId");
    
    if (userIdFromToken == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    if (!userIdFromToken.equals(teamRequestDto.getUserId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    TeamResponseDto responseDto = teamService.createTeam(teamRequestDto);
    return ResponseEntity.ok(responseDto);
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
    if (!team.getUser().getUserId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
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
    if (userId == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Add player and get updated team
    Team updatedTeam = teamService.addPlayerToTeam(
        teamId, 
        addPlayerRequest.getPosition(), 
        addPlayerRequest.getPlayerInfo(), 
        userId
    );

    // Convert to DTO and return
    return ResponseEntity.ok(teamMapper.toResponseDto(updatedTeam));
}

}