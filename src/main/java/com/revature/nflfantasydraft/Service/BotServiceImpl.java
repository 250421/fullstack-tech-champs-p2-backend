package com.revature.nflfantasydraft.Service;

import com.revature.nflfantasydraft.Config.OpenAIConfig;
import com.revature.nflfantasydraft.Dto.*;
import com.revature.nflfantasydraft.Entity.Bot;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Entity.Team;
import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.EBotException;
import com.revature.nflfantasydraft.Exceptions.ETeamException;
import com.revature.nflfantasydraft.Repository.BotRepository;
import com.revature.nflfantasydraft.Repository.PlayerRepository;
import com.revature.nflfantasydraft.Repository.TeamRepository;
import com.revature.nflfantasydraft.Repository.UserRepository;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
    public Bot getBotById(Long botId) {
        return botRepository.findById(botId)
            .orElseThrow(() -> new EBotException("Bot not found"));
    }

    @Override
    public Bot updateBot(Bot bot) {
        if (!botRepository.existsById(bot.getBotId())) {
            throw new EBotException("Bot not found");
        }
        return botRepository.save(bot);
    }

    @Override
public TeamResponseDto createBotTeam(BotTeamRequestDto botTeamRequestDto) {
    System.out.println("INSIDE BOT SERVICE");
    try {
        System.out.println("ABOUT TO CALL OPENAI");
        // Initialize OpenAI service using the configured API key
        OpenAiService openAiService = new OpenAiService(openAIConfig.getApiKey());
        System.out.println("AFTER TO CALL OPENAI");
        
        System.out.println("ABOUT TO FIND BOT");
        // Validate bot exists
        Bot bot = botRepository.findById(botTeamRequestDto.getBotId())
                .orElseThrow(() -> new EBotException("Bot not found with ID: " + botTeamRequestDto.getBotId()));
        
        System.out.println("AFTER TO FIND BOT");
        
        // Generate team name using OpenAI if not provided
        String teamName = botTeamRequestDto.getTeamName();
        System.out.println("AFTER TEAM NAME");
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

        System.out.println("AFTER IF");
        
        // Create or get system user for bots with proper error handling
        User botUser;
        try {
            Optional<User> botUserOptional = userRepository.findByEmail("bot@system.com");
            System.out.println("AFTER FIND BY EMAIL");
            botUser = botUserOptional.orElseGet(() -> {
                User newBotUser = new User();
                newBotUser.setUserName("BotUser");
                newBotUser.setEmail("bot@system.com");
                newBotUser.setPassword("botpassword");
                newBotUser.setRole("BOT");
                return userRepository.save(newBotUser);
            });
            System.out.println("AFTER ALL");
        } catch (Exception e) {
            System.out.println("INSIDE CATCH");
            throw new EBotException("Failed to create/find bot user: " + e.getMessage());
        }

        System.out.println("PART 3");
        
        // Create and save team
        try {
            Team team = new Team();
            System.out.println("PART 4");
            team.setTeamName(teamName);
            team.setUser(botUser);
            team.setBot(bot);
            team.setLeagueId(botTeamRequestDto.getLeagueId());
            team.setIsBot(true);
            team.setImgUrl("https://example.com/bot-team.png");
            
            Team savedTeam = teamRepository.save(team);

            System.out.println("PART 6");
            
            // Update bot with team ID
            bot.setTeamId(savedTeam.getTeamId());
            botRepository.save(bot);

            System.out.println("PART 7");
            
            return teamService.convertToResponseDto(savedTeam);
        } catch (Exception e) {

            System.out.println("PART 8");
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
public BotPickResponseDto  botPickPlayer(Long teamId) {
    
    System.out.println("TESTING INITIAL 1");
    
    // Initialize OpenAI service
    OpenAiService openAiService = new OpenAiService(openAIConfig.getApiKey());
    System.out.println("TESTING INITIAL 2");
    // Fetch bot and team
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new EBotException("Team not found"));

    System.out.println("TESTING INITIAL 3");
    if (!Boolean.TRUE.equals(team.getIsBot())) {
        System.out.println("TESTING INITIAL 4");
        throw new EBotException("Only bot teams can use this feature");
    }
    System.out.println("TESTING INITIAL 5");
    // Get all undrafted players as DTOs
    List<UndraftedPlayerDto> availablePlayers = playerRepository.findUndraftedPlayers();
    System.out.println("TESTING INITIAL 6");
    
    if (availablePlayers.isEmpty()) {
        System.out.println("TESTING INITIAL 7");
        throw new EBotException("No available undrafted players");
    }

    System.out.println("TESTING INITIAL 8");
    
    // Determine which positions still need players
    List<String> neededPositions = getNeededPositions(team);

    System.out.println("TESTING INITIAL 9");
    if (neededPositions.isEmpty()) {
        System.out.println("TESTING INITIAL 10");
        throw new EBotException("All positions are already filled");
    }

    System.out.println("TESTING INITIAL 11");
    
    // Filter players to only include needed positions
    List<UndraftedPlayerDto> eligiblePlayers = availablePlayers.stream()
            .filter(p -> neededPositions.contains(p.getPosition().toUpperCase()))
            .collect(Collectors.toList());
    System.out.println("TESTING INITIAL 12");
    
    if (eligiblePlayers.isEmpty()) {
        System.out.println("TESTING INITIAL 13");
        throw new EBotException("No available players for needed positions: " + neededPositions);
    }

    System.out.println("TESTING INITIAL 14");
    
    // Build the prompt for AI to select a player
    String prompt = String.format(
        "Strategy: %s\nDifficulty: %s\nNeeded Positions: %s\nAvailable Players:\n%s\n" +
        "Rules: 1) Only pick players for needed positions\n" +
        "2) Return ONLY their playerApiId as a number."+
        "3) Only pick players with > 0 fantasyPoints",
        team.getBot().getStrategy(),
        team.getBot().getDifficultyLevel(),
        String.join(", ", neededPositions),
        eligiblePlayers.stream()
            .map(p -> String.format("- %s (ID: %d, Pos: %s, Tea: %s, Pts: %.1f)", 
                p.getName(), p.getPlayerApiId(), p.getTeam(), p.getPosition(), p.getFantasyPoints()))
            .collect(Collectors.toList())
    );
    
    System.out.println("TESTING INITIAL 15");
    
    // Get AI response
    ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
    System.out.println("TESTING INITIAL 16");
    
    chatCompletionRequest.setModel("gpt-4.1-nano");
    System.out.println("TESTING INITIAL 16.1");
    chatCompletionRequest.setMessages(Arrays.asList(
        new ChatMessage("system", "You are an NFL fantasy draft assistant. Return ONLY the numeric playerApiId for a player in a needed position."),
        new ChatMessage("user", prompt)
    ));
    System.out.println("TESTING INITIAL 16.2");
    chatCompletionRequest.setMaxTokens(10);
    System.out.println("TESTING INITIAL 16.3");
    chatCompletionRequest.setTemperature(0.5);

    System.out.println("TESTING INITIAL 16.4");

    String response = openAiService.createChatCompletion(chatCompletionRequest)
        .getChoices()
        .get(0)
        .getMessage()
        .getContent()
        .trim();

    System.out.println("TESTING INITIAL 17");
        
    try {
        Integer playerApiId = Integer.parseInt(response);
        
        // Find the selected player
        UndraftedPlayerDto selectedPlayer = eligiblePlayers.stream()
            .filter(p -> p.getPlayerApiId().equals(playerApiId))
            .findFirst()
            .orElseThrow(() -> new EBotException("Selected player not found or not eligible"));

        System.out.println("TESTING INITIAL 18");
        
        // Verify position is still needed (race condition check)
        if (!neededPositions.contains(selectedPlayer.getPosition().toUpperCase())) {
            System.out.println("TESTING INITIAL 19");
            throw new EBotException("Selected position " + selectedPlayer.getPosition() + " is already filled");
        }

        System.out.println("TESTING INITIAL 20");
        // Get all player records for this API ID and mark them as drafted
        List<Player> players = playerRepository.findAllByPlayerApiId(playerApiId);
        players.forEach(player -> {
            player.setIsDrafted(true);
            playerRepository.save(player);
        });
        System.out.println("TESTING INITIAL 21");

        // Calculate total points
        Double totalPoints = players.stream()
            .mapToDouble(Player::getFantasyPoints)
            .sum();

        System.out.println("TESTING INITIAL 22");

        // Update team
        String playerInfo = String.format("%s, %s, %s, %.1f",
            selectedPlayer.getName(),
            selectedPlayer.getTeam(),
            selectedPlayer.getPlayerApiId(),
            totalPoints);

        System.out.println("TESTING INITIAL 23");
        
        // Update the appropriate position
        switch (selectedPlayer.getPosition().toUpperCase()) {
            case "QB": team.setQb(playerInfo); break;
            case "RB": team.setRb(playerInfo); break;
            case "WR": team.setWr(playerInfo); break;
            case "TE": team.setTe(playerInfo); break;
            case "K": team.setK(playerInfo); break;
            default: throw new EBotException("Invalid position: " + selectedPlayer.getPosition());
        }

        System.out.println("TESTING INITIAL 24");
        
        Team updatedTeam = teamRepository.save(team);
        
        System.out.println("TESTING INITIAL 25");
        // Create and return response
        BotPickResponseDto responseDto = new BotPickResponseDto();
        responseDto.setTeam(teamService.convertToResponseDto(updatedTeam));
        responseDto.setPickedPlayerName(selectedPlayer.getName());

        System.out.println("TESTING INITIAL 26");
        return responseDto;
        
    } catch (NumberFormatException e) {
        throw new EBotException("Invalid player ID from AI: " + response);
    }
}

// Helper method to determine which positions need players
private List<String> getNeededPositions(Team team) {
    List<String> neededPositions = new ArrayList<>();
    
    if (team.getQb() == null || team.getQb().isEmpty()) {
        neededPositions.add("QB");
    }
    if (team.getRb() == null || team.getRb().isEmpty()) {
        neededPositions.add("RB");
    }
    if (team.getWr() == null || team.getWr().isEmpty()) {
        neededPositions.add("WR");
    }
    if (team.getTe() == null || team.getTe().isEmpty()) {
        neededPositions.add("TE");
    }
    if (team.getK() == null || team.getK().isEmpty()) {
        neededPositions.add("K");
    }
    
    return neededPositions;
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

   
