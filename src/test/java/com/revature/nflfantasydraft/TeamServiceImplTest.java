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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

        MockitoAnnotations.openMocks(this);
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
    assertEquals("Patrick Mahomes, KC, 19790, 55.0", result.getQb());
    
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

    @Test
    void createTeam_ShouldSetLeagueId() {
        TeamRequestDto request = new TeamRequestDto();
        request.setTeamName("Test Team");
        request.setUserId(1);
        request.setLeagueId(5L);

        User user = new User();
        user.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(teamRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TeamResponseDto result = teamService.createTeam(request);
        
        assertEquals(5L, result.getLeagueId());
    }

    @Test
    void getTeamsByLeagueId_ShouldFilterByLeague() {
        Team team1 = new Team();
        team1.setLeagueId(1L);
        Team team2 = new Team();
        team2.setLeagueId(2L);

        when(teamRepository.findByLeagueId(1L)).thenReturn(Collections.singletonList(team1));
        
        List<TeamResponseDto> result = teamService.getTeamsByLeagueId(1L);
        
        assertEquals(1, result.size());
    }

    @Test
void addPlayerToTeam_ShouldMarkPlayerAsDrafted() {
    // 1. Setup test team and user
    Team team = new Team();
    User user = new User();
    user.setUserId(1);
    team.setUser(user);

    // 2. Create test players with fantasy points
    Player player1 = new Player();
    player1.setPlayerApiId(123);
    player1.setPosition("QB");
    player1.setFantasyPoints(25.0);
    player1.setIsDrafted(false); // Initially undrafted

    Player player2 = new Player();
    player2.setPlayerApiId(123); // Same API ID
    player2.setPosition("QB");
    player2.setFantasyPoints(30.0);
    player2.setIsDrafted(false);

    // 3. Configure mocks
    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
    when(playerRepository.findAllByPlayerApiId(123)).thenReturn(Arrays.asList(player1, player2));
    when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
        Team savedTeam = invocation.getArgument(0);
        // Verify player was marked as drafted
        assertTrue(player1.getIsDrafted());
        assertTrue(player2.getIsDrafted());
        return savedTeam;
    });

    // 4. Execute
    Team result = teamService.addPlayerToTeam(1L, "QB", 123, 1);

    // 5. Verify
    assertNotNull(result);
    verify(playerRepository, times(2)).save(any(Player.class)); // Each player should be saved
}
    
    @Test
    void getTeamsByLeagueId_ShouldReturnTeams() {
        // 1. Create test data
        Team team = new Team();
        team.setTeamId(1L);
        team.setTeamName("Test Team");
        team.setLeagueId(5L);

        // 2. Configure mock
        when(teamRepository.findByLeagueId(5L)).thenReturn(Collections.singletonList(team));

        // 3. Execute test
        List<TeamResponseDto> result = teamService.getTeamsByLeagueId(5L);

        // 4. Verify results
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getLeagueId());
        assertEquals("Test Team", result.get(0).getTeamName());
        
        // 5. Verify interaction
        verify(teamRepository).findByLeagueId(5L);
    }

    @Test
    void getTeamsByLeagueId_ShouldReturnEmptyListWhenNoTeams() {
        // Configure mock to return empty list
        when(teamRepository.findByLeagueId(anyLong())).thenReturn(Collections.emptyList());

        List<TeamResponseDto> result = teamService.getTeamsByLeagueId(99L);
        
        assertTrue(result.isEmpty());
    }

}