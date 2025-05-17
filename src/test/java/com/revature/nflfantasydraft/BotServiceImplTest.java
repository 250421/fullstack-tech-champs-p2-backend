package com.revature.nflfantasydraft;

import com.revature.nflfantasydraft.Entity.Bot;
import com.revature.nflfantasydraft.Entity.Team;
import com.revature.nflfantasydraft.Exceptions.EBotException;
import com.revature.nflfantasydraft.Repository.BotRepository;
import com.revature.nflfantasydraft.Repository.TeamRepository;
import com.revature.nflfantasydraft.Repository.UserRepository;
import com.revature.nflfantasydraft.Service.BotServiceImpl;
import com.revature.nflfantasydraft.Service.TeamService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BotServiceImplTest {

    @Mock
    private BotRepository botRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private BotServiceImpl botService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteBot_ShouldDeleteBotAndTeam() {
        Bot bot = new Bot();
        bot.setBotId(1L);
        bot.setTeamId(1L);

        when(botRepository.findById(1L)).thenReturn(Optional.of(bot));
        
        botService.deleteBot(1L);
        
        verify(teamRepository).deleteById(1L);
        verify(botRepository).deleteById(1L);
    }

    @Test
    void deleteBotTeam_ShouldDeleteOnlyBotTeam() {
        Team team = new Team();
        team.setTeamId(1L);
        team.setIsBot(true);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        
        botService.deleteBotTeam(1L);
        
        verify(teamRepository).deleteById(1L);
    }

    @Test
    void deleteBotTeam_ShouldThrowForNonBotTeam() {
        Team team = new Team();
        team.setTeamId(1L);
        team.setIsBot(false);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        
        assertThrows(EBotException.class, () -> botService.deleteBotTeam(1L));
    }

    
}