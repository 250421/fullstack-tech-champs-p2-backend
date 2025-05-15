package com.revature.nflfantasydraft.Service;

import com.revature.nflfantasydraft.Config.OpenAIConfig;
import com.revature.nflfantasydraft.Dto.*;
import com.revature.nflfantasydraft.Entity.Bot;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Entity.Team;
import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.EBotException;
import com.revature.nflfantasydraft.Repository.BotRepository;
import com.revature.nflfantasydraft.Repository.PlayerRepository;
import com.revature.nflfantasydraft.Repository.TeamRepository;
import com.revature.nflfantasydraft.Repository.UserRepository;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BotServiceImpl implements BotService {

    private final BotRepository botRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamService teamService;
    private final OpenAIConfig openAIConfig;
    private final PlayerRepository playerRepository;

    
    private static final Logger logger = LoggerFactory.getLogger(BotServiceImpl.class);
    
    public BotServiceImpl(BotRepository botRepository,
                        TeamRepository teamRepository,
                        UserRepository userRepository,
                        TeamService teamService,
                        OpenAIConfig openAIConfig,
                        PlayerRepository playerRepository) {
        this.botRepository = botRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamService = teamService;
        this.openAIConfig = openAIConfig;
        this.playerRepository = playerRepository;
    }

    @Override
    public BotResponseDto createBot(BotRequestDto botRequestDto) {
        Bot bot = new Bot();
        bot.setLeagueId(botRequestDto.getLeagueId());
        bot.setTeamId(botRequestDto.getTeamId());
        bot.setDifficultyLevel(botRequestDto.getDifficultyLevel());
        bot.setStrategy(botRequestDto.getStrategy());
        
        Bot savedBot = botRepository.save(bot);
        return convertToDto(savedBot);
    }

    @Override
public TeamResponseDto createBotTeam(BotTeamRequestDto botTeamRequestDto) {
    try {
        // Initialize OpenAI service using the configured API key
        OpenAiService openAiService = new OpenAiService(openAIConfig.getApiKey());
        
        // Validate bot exists
        Bot bot = botRepository.findById(botTeamRequestDto.getBotId())
                .orElseThrow(() -> new EBotException("Bot not found with ID: " + botTeamRequestDto.getBotId()));
        
        // Generate team name using OpenAI if not provided
        String teamName = botTeamRequestDto.getTeamName();
        if (teamName == null || teamName.isEmpty()) {
            try {
                CompletionRequest completionRequest = CompletionRequest.builder()
                        .prompt("Generate a creative fantasy football team name for a bot team")
                        .model("gpt-3.5-turbo-instruct")
                        .maxTokens(50)
                        .temperature(0.7)
                        .build();
                
                teamName = openAiService.createCompletion(completionRequest)
                        .getChoices()
                        .get(0)
                        .getText()
                        .trim();
            } catch (Exception e) {
                throw new EBotException("Failed to generate team name: " + e.getMessage());
            }
        }
        
        // Create or get system user for bots with proper error handling
        User botUser;
        try {
            Optional<User> botUserOptional = userRepository.findByEmail("bot@system.com");
            botUser = botUserOptional.orElseGet(() -> {
                User newBotUser = new User();
                newBotUser.setUserName("BotUser");
                newBotUser.setEmail("bot@system.com");
                newBotUser.setPassword("botpassword");
                newBotUser.setRole("BOT");
                return userRepository.save(newBotUser);
            });
        } catch (Exception e) {
            throw new EBotException("Failed to create/find bot user: " + e.getMessage());
        }
        
        // Create and save team
        try {
            Team team = new Team();
            team.setTeamName(teamName);
            team.setUser(botUser);
            team.setBot(bot);
            team.setLeagueId(botTeamRequestDto.getLeagueId());
            team.setIsBot(true);
            team.setImgUrl("https://example.com/bot-team.png");
            
            Team savedTeam = teamRepository.save(team);
            
            // Update bot with team ID
            bot.setTeamId(savedTeam.getTeamId());
            botRepository.save(bot);
            
            return teamService.convertToResponseDto(savedTeam);
        } catch (Exception e) {
            throw new EBotException("Failed to create team: " + e.getMessage());
        }
    } catch (EBotException e) {
        // Re-throw EBotException to be handled by controller
        throw e;
    } catch (Exception e) {
        // Catch any unexpected exceptions and wrap them in EBotException
        throw new EBotException("Unexpected error creating bot team: " + e.getMessage());
    }
}

@Override
public TeamResponseDto botPickPlayer(BotPickPlayerRequestDto botPickPlayerRequestDto) {
    // Initialize OpenAI service
    OpenAiService openAiService = new OpenAiService(openAIConfig.getApiKey());
    
    // Fetch bot and team
    Bot bot = botRepository.findById(botPickPlayerRequestDto.getBotId())
            .orElseThrow(() -> new EBotException("Bot not found"));
    
    Team team = teamRepository.findById(botPickPlayerRequestDto.getTeamId())
            .orElseThrow(() -> new EBotException("Team not found"));
    
    if (!team.getIsBot()) {
        throw new EBotException("Only bot teams can use this feature");
    }
    
    // Get available players
    List<Player> availablePlayers = teamService.getPlayersByPositionWithTotalPoints(
            botPickPlayerRequestDto.getPosition());
    
    if (availablePlayers.isEmpty()) {
        throw new EBotException("No available players for position: " + 
            botPickPlayerRequestDto.getPosition());
    }
    
    // Build the prompt
    String prompt = String.format(
        "Given the following bot strategy: %s (difficulty: %s), " +
        "select the best %s from these players with their total fantasy points: %s. " +
        "Return ONLY the numeric playerApiId with NO additional text.",
        bot.getStrategy(),
        bot.getDifficultyLevel(),
        botPickPlayerRequestDto.getPosition(),
        availablePlayers.stream()
            .map(p -> String.format("%s (ID: %d, Points: %.1f)", 
                p.getName(), p.getPlayerApiId(), p.getFantasyPoints()))
            .collect(Collectors.joining(", "))
    );
    
    // Get AI response
    CompletionRequest completionRequest = CompletionRequest.builder()
            .prompt(prompt)
            .model("gpt-3.5-turbo-instruct")
            .maxTokens(10)
            .temperature(0.5)
            .build();
    
    String response = openAiService.createCompletion(completionRequest)
            .getChoices()
            .get(0)
            .getText()
            .trim();
    
    try {
        Integer playerApiId = Integer.parseInt(response);
        
        // Get all player records for this API ID and mark them as drafted
        List<Player> players = playerRepository.findAllByPlayerApiId(playerApiId);
        players.forEach(player -> {
            player.setIsDrafted(true);
            playerRepository.save(player);
        });

        // Get aggregated player data
        List<Object[]> playerData = playerRepository.findPlayerSummaryByApiId(playerApiId);
        
        if (playerData.isEmpty()) {
            throw new EBotException("Selected player not found: " + playerApiId);
        }
        
        Object[] playerSummary = playerData.get(0);
        
        // Safely extract values with explicit type conversion
        String playerName = (String) playerSummary[0];
        String playerTeam = (String) playerSummary[1];
        double totalPoints = ((Number) playerSummary[2]).doubleValue(); // Critical fix
        
        // Format player info - ensure numeric formatting
        String playerInfo;
        try {
            playerInfo = String.format("%s,%s,%.1f",
                playerName != null ? playerName : "Unknown",
                playerTeam != null ? playerTeam : "Unknown",
                totalPoints);
        } catch (IllegalFormatConversionException e) {
            logger.error("Formatting failed - totalPoints: {}", totalPoints);
            throw new EBotException("Player data formatting error");
        }
        
        // Update team position
        switch (botPickPlayerRequestDto.getPosition().toUpperCase()) {
            case "QB": team.setQb(playerInfo); break;
            case "RB": team.setRb(playerInfo); break;
            case "WR": team.setWr(playerInfo); break;
            case "TE": team.setTe(playerInfo); break;
            case "K": team.setK(playerInfo); break;
            default: throw new EBotException("Invalid position");
        }
        
        Team updatedTeam = teamRepository.save(team);

        logger.debug("Player Summary: name={}, team={}, points={}", 
        playerName, playerTeam, totalPoints);

        return teamService.convertToResponseDto(updatedTeam);
        
    } catch (NumberFormatException e) {
        throw new EBotException("Invalid player ID from AI: " + response);
    }
}

    private BotResponseDto convertToDto(Bot bot) {
        BotResponseDto dto = new BotResponseDto();
        dto.setBotId(bot.getBotId());
        dto.setLeagueId(bot.getLeagueId());
        dto.setTeamId(bot.getTeamId());
        dto.setDifficultyLevel(bot.getDifficultyLevel());
        dto.setStrategy(bot.getStrategy());
        return dto;
    }

    @Override
    public List<TeamResponseDto> getAllBotTeams() {
        try {
            List<Team> botTeams = teamRepository.findAll().stream()
                    .filter(t -> t.getIsBot() != null && t.getIsBot())
                    .collect(Collectors.toList());
            
            return botTeams.stream()
                    .map(team -> {
                        try {
                            return teamService.convertToResponseDto(team);
                        } catch (Exception e) {
                            logger.error("Error converting team with ID: {}", 
                                team != null ? team.getTeamId() : "null", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving bot teams", e);
            throw new EBotException("Error retrieving bot teams: " + e.getMessage());
        }
    }

    @Override
    public List<BotResponseDto> getAllBots() {
        try {
            List<Bot> bots = botRepository.findAll();
            return bots.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving all bots", e);
            throw new EBotException("Failed to retrieve bots: " + e.getMessage());
        }
    }


    @Override
    public void deleteBot(Long botId) {
        // Check if bot exists
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new EBotException("Bot not found with ID: " + botId));
        
        // Delete associated team first if exists
        if (bot.getTeamId() != null) {
            teamRepository.deleteById(bot.getTeamId());
        }
        
        // Delete the bot
        botRepository.deleteById(botId);
    }
    
    @Override
    public void deleteBotTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EBotException("Team not found with ID: " + teamId));
        
        if (!Boolean.TRUE.equals(team.getIsBot())) {
            throw new EBotException("Only bot teams can be deleted with this endpoint");
        }
        
        // Delete the team
        teamRepository.deleteById(teamId);
        
        // Optionally: Also delete the bot associated with this team
        botRepository.findByTeamId(teamId).ifPresent(bot -> {
            bot.setTeamId(null);
            botRepository.save(bot);
        });
    }

}

   