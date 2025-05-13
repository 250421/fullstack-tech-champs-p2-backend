package com.revature.nflfantasydraft;

import com.revature.nflfantasydraft.Dto.TeamRequestDto;
import com.revature.nflfantasydraft.Dto.TeamResponseDto;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Entity.Team;
import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.ETeamException;
import com.revature.nflfantasydraft.Repository.PlayerRepository;
import com.revature.nflfantasydraft.Repository.TeamRepository;
import com.revature.nflfantasydraft.Repository.UserRepository;
import com.revature.nflfantasydraft.Service.TeamServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Team team;
    private User user;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setUserName("testUser");

        team = new Team();
        team.setTeamId(1L);
        team.setTeamName("Test Team");
        team.setUser(user);

        player1 = new Player();
        player1.setPlayerApiId(19790);
        player1.setName("Patrick Mahomes");
        player1.setTeam("KC");
        player1.setPosition("QB");
        player1.setFantasyPoints(25.0);

        player2 = new Player();
        player2.setPlayerApiId(19790);
        player2.setName("Patrick Mahomes");
        player2.setTeam("KC");
        player2.setPosition("QB");
        player2.setFantasyPoints(30.0);
    }

    @Test
void addPlayerToTeam_Success() {
    // 1. Setup test data
    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
    when(playerRepository.findAllByPlayerApiId(19790)).thenReturn(Arrays.asList(player1, player2));
    
    // 2. Configure the save method to return the team
    when(teamRepository.save(any(Team.class))).thenReturn(team);  // This was missing

    // 3. Execute the test
    Team result = teamService.addPlayerToTeam(1L, "QB", 19790, 1);

    // 4. Verify results
    assertNotNull(result);
    assertEquals("Patrick Mahomes,KC,55.0", result.getQb());
    
    // 5. Verify interactions
    verify(teamRepository).save(team);
    verify(playerRepository).findAllByPlayerApiId(19790);
}

    @Test
    void addPlayerToTeam_TeamNotFound() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ETeamException.class, () -> 
            teamService.addPlayerToTeam(1L, "QB", 19790, 1));
    }

    @Test
    void addPlayerToTeam_UnauthorizedUser() {
        Team otherTeam = new Team();
        otherTeam.setTeamId(1L);
        otherTeam.setUser(new User(2, "otherUser", "other@example.com", "pass", "USER"));

        when(teamRepository.findById(1L)).thenReturn(Optional.of(otherTeam));

        assertThrows(ETeamException.class, () -> 
            teamService.addPlayerToTeam(1L, "QB", 19790, 1));
    }

    @Test
    void addPlayerToTeam_PlayerNotFound() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(playerRepository.findAllByPlayerApiId(19790)).thenReturn(Collections.emptyList());

        assertThrows(ETeamException.class, () -> 
            teamService.addPlayerToTeam(1L, "QB", 19790, 1));
    }

    @Test
    void addPlayerToTeam_PositionMismatch() {
        player1.setPosition("RB");
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(playerRepository.findAllByPlayerApiId(19790)).thenReturn(Arrays.asList(player1, player2));

        assertThrows(ETeamException.class, () -> 
            teamService.addPlayerToTeam(1L, "QB", 19790, 1));
    }

    @Test
    void createTeam_Success() {
        // Test data setup
        TeamRequestDto requestDto = new TeamRequestDto();
        requestDto.setTeamName("New Team");
        requestDto.setUserId(1);
        
        Team newTeam = new Team();
        newTeam.setTeamName("New Team");
        newTeam.setUser(user);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(teamRepository.save(any(Team.class))).thenReturn(newTeam);

        TeamResponseDto result = teamService.createTeam(requestDto);

        assertNotNull(result);
        assertEquals("New Team", result.getTeamName());
        assertEquals(1, result.getUserId());
    }

    @Test
    void getTeamById_Success() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        Team result = teamService.getTeamById(1L);

        assertNotNull(result);
        assertEquals("Test Team", result.getTeamName());
    }

    @Test
    void getTeamById_NotFound() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ETeamException.class, () -> teamService.getTeamById(1L));
    }

    @Test
    void getTeamsByUserId_Success() {
        when(teamRepository.findByUserUserId(1)).thenReturn(Collections.singletonList(team));

        List<Team> result = teamService.getTeamsByUserId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Team", result.get(0).getTeamName());
    }
}