package com.revature.nflfantasydraft;

import com.revature.nflfantasydraft.Config.OpenAIConfig;
import com.revature.nflfantasydraft.Dto.*;
import com.revature.nflfantasydraft.Entity.*;
import com.revature.nflfantasydraft.Exceptions.EBotException;
import com.revature.nflfantasydraft.Repository.*;
import com.revature.nflfantasydraft.Service.*;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotServiceImplTest {

    @Mock private BotRepository botRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private UserRepository userRepository;
    @Mock private TeamService teamService;
    @Mock private OpenAIConfig openAIConfig;
    @Mock private PlayerRepository playerRepository;
    @Mock private OpenAiService openAiService;
    
    @InjectMocks private BotServiceImpl botService;

    private Team team;
    private Bot bot;
    private Player player1, player2;
    private UndraftedPlayerDto undraftedPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize bot
    bot = new Bot();
    bot.setBotId(1L);
    bot.setTeamId(1L);
    bot.setDifficultyLevel("EASY");
    bot.setStrategy("BALANCED");
    
    // Initialize team with bot relationship
    team = new Team();
    team.setTeamId(1L);
    team.setTeamName("Test Bot Team");
    team.setIsBot(true);
    team.setBot(bot);  // THIS IS THE CRUCIAL LINE THAT WAS MISSING
    
    // Set bot's team reference
    bot.setTeam(team);
        
        User user = new User();
        user.setUserId(-1);
        user.setUserName("BotUser");
        team.setUser(user);

        player1 = new Player();
        player1.setPlayerApiId(123);
        player1.setName("Test Player");
        player1.setTeam("TB");
        player1.setPosition("QB");
        player1.setFantasyPoints(25.0);
        player1.setIsDrafted(false);

        player2 = new Player();
        player2.setPlayerApiId(123);
        player2.setName("Test Player");
        player2.setTeam("TB");
        player2.setPosition("QB");
        player2.setFantasyPoints(30.0);
        player2.setIsDrafted(false);

        undraftedPlayer = new UndraftedPlayerDto(
            123, "Test Player", "TB", "QB", 55.0, false
        );
    }

    // Original test cases
    @Test
    void deleteBot_ShouldDeleteBotAndTeam() {
        when(botRepository.findById(1L)).thenReturn(Optional.of(bot));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        
        botService.deleteBot(1L);
        
        verify(teamRepository).deleteById(1L);
        verify(botRepository).deleteById(1L);
    }

    @Test
    void deleteBotTeam_ShouldDeleteOnlyBotTeam() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        
        botService.deleteBotTeam(1L);
        
        verify(teamRepository).deleteById(1L);
    }

    @Test
    void deleteBotTeam_ShouldThrowForNonBotTeam() {
        team.setIsBot(false);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        
        assertThrows(EBotException.class, () -> botService.deleteBotTeam(1L));
    }

    // New test cases for botPickPlayer functionality
    @Test
    void botPickPlayer_ShouldReturnCompleteResponse() throws EBotException {
        // 1. Setup complete mock response chain
        var chatCompletionResult = new com.theokanning.openai.completion.chat.ChatCompletionResult();
        var choice = new com.theokanning.openai.completion.chat.ChatCompletionChoice();
        var message = new com.theokanning.openai.completion.chat.ChatMessage("assistant", "123");
        choice.setMessage(message);
        chatCompletionResult.setChoices(Collections.singletonList(choice));
    
        // 2. Mock the entire OpenAI service call chain
        when(openAIConfig.getApiKey()).thenReturn("${OPENAI_API_KEY}");
        when(openAiService.createChatCompletion(any()))
            .thenReturn(chatCompletionResult);
    
        // 3. Mock repository responses
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(playerRepository.findUndraftedPlayers()).thenReturn(
            Collections.singletonList(undraftedPlayer)
        );
        when(playerRepository.findAllByPlayerApiId(123)).thenReturn(
            Arrays.asList(player1, player2)
        );
        when(teamRepository.save(any())).thenReturn(team);
        
        TeamResponseDto mockTeamResponse = new TeamResponseDto();
        mockTeamResponse.setTeamId(1L);
        when(teamService.convertToResponseDto(any())).thenReturn(mockTeamResponse);
    
        // 4. Execute
        BotPickResponseDto result = botService.botPickPlayer(1L);
    
        // 5. Verify
        assertNotNull(result);
        assertEquals("Test Player", result.getPickedPlayerName());
    }

    @Test
    void botPickPlayer_ShouldThrowWhenNoEligiblePlayers() {
        // 1. Set up the team with bot relationship
        Team team = new Team();
        team.setTeamId(1L);
        team.setIsBot(true);
        team.setBot(bot); // Make sure bot is set
        
        // 2. Mock the repository responses
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(playerRepository.findUndraftedPlayers()).thenReturn(Collections.emptyList());
        
        // 3. Mock the OpenAI config (even though we shouldn't reach it)
        when(openAIConfig.getApiKey()).thenReturn("${OPENAI_API_KEY}");
        
        // 4. Execute and verify
        EBotException exception = assertThrows(EBotException.class, 
            () -> botService.botPickPlayer(1L));
        
        // 5. Additional verification
        assertEquals("No available undrafted players", exception.getMessage());
        
        // Verify OpenAI service was never called
        verify(openAiService, never()).createChatCompletion(any());
    }

@Test
void botPickPlayer_ShouldMarkPlayersAsDrafted() throws EBotException {
    // 1. Setup complete mock response chain
    var chatCompletionResult = new com.theokanning.openai.completion.chat.ChatCompletionResult();
    var choice = new com.theokanning.openai.completion.chat.ChatCompletionChoice();
    var message = new com.theokanning.openai.completion.chat.ChatMessage("assistant", "123");
    choice.setMessage(message);
    chatCompletionResult.setChoices(Collections.singletonList(choice));

    // 2. Mock the entire OpenAI service call chain
    when(openAIConfig.getApiKey()).thenReturn("${OPENAI_API_KEY}");
    when(openAiService.createChatCompletion(any()))
        .thenReturn(chatCompletionResult);

    // 3. Mock repository responses
    when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
    when(playerRepository.findUndraftedPlayers()).thenReturn(
        Collections.singletonList(undraftedPlayer)
    );
    when(playerRepository.findAllByPlayerApiId(123)).thenReturn(
        Arrays.asList(player1, player2)
    );
    when(teamRepository.save(any())).thenReturn(team);
    when(teamService.convertToResponseDto(any())).thenReturn(new TeamResponseDto());

    // 4. Execute
    botService.botPickPlayer(1L);

    // 5. Verify
    assertTrue(player1.getIsDrafted());
    assertTrue(player2.getIsDrafted());
}

}