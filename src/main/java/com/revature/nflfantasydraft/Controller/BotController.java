package com.revature.nflfantasydraft.Controller;

import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.nflfantasydraft.Dto.BotPickPlayerRequestDto;
import com.revature.nflfantasydraft.Dto.BotPickResponseDto;
import com.revature.nflfantasydraft.Dto.BotRequestDto;
import com.revature.nflfantasydraft.Dto.BotResponseDto;
import com.revature.nflfantasydraft.Dto.BotTeamRequestDto;
import com.revature.nflfantasydraft.Dto.TeamResponseDto;
import com.revature.nflfantasydraft.Entity.Bot;
import com.revature.nflfantasydraft.Exceptions.EBotException;
import com.revature.nflfantasydraft.Service.BotService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/bots")
public class BotController {

    @Autowired
    private BotService botService;

    private static final Logger logger = LoggerFactory.getLogger(BotController.class);

    @PostMapping
    public ResponseEntity<BotResponseDto> createBot(@RequestBody BotRequestDto botRequestDto) {
        return ResponseEntity.ok(botService.createBot(botRequestDto));
    }

    @PutMapping("/{botId}")
    public ResponseEntity<Bot> updateBot(@PathVariable Long botId, @RequestBody Bot bot, 
                                         HttpServletRequest request) {
        // Integer userId = (Integer) request.getAttribute("userId");
        // Bot existingBot = botService.getBotById(botId);
        // if (!existingBot.getUser().getUserId().equals(userId)) {
        //     return ResponseEntity.status(403).build();
        // }
        bot.setBotId(botId);
        return ResponseEntity.ok(botService.updateBot(bot));
    }

    @PostMapping("/teams")
public ResponseEntity<?> createBotTeam(@RequestBody BotTeamRequestDto botTeamRequestDto) {
    System.out.println("INSIDE ADD TEAM HERE");
    try {
        System.out.println("BEFORE SERVICE");
        TeamResponseDto response = botService.createBotTeam(botTeamRequestDto);
        return ResponseEntity.ok(response);
    } catch (EBotException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}

@GetMapping("/teams")
public ResponseEntity<?> getAllBotTeams() {
    try {
        List<TeamResponseDto> botTeams = botService.getAllBotTeams();
        
        if (botTeams == null || botTeams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No bot teams found"));
        }
        
        return ResponseEntity.ok(botTeams);
    } catch (Exception e) {
        logger.error("Error in getAllBotTeams: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to retrieve bot teams",
                    "details", e.getMessage() != null ? e.getMessage() : "Unknown error"
                ));
    }
}

@GetMapping
    public ResponseEntity<?> getAllBots() {
        try {
            List<BotResponseDto> bots = botService.getAllBots();
            if (bots.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No bots found"));
            }
            return ResponseEntity.ok(bots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve bots: " + e.getMessage()));
        }
    }


    @PostMapping("/{teamId}/pick-player")
    public ResponseEntity<Map<String, String>> botPickPlayer(@PathVariable Long teamId) {
        BotPickResponseDto response = botService.botPickPlayer(teamId);
        String message =  response.getPickedPlayerName();
        return ResponseEntity.ok(Collections.singletonMap("message", message));
    }

@DeleteMapping("/teams/{teamId}")
public ResponseEntity<?> deleteBotTeam(@PathVariable Long teamId) {
    try {
        botService.deleteBotTeam(teamId);
        return ResponseEntity.ok().build();
    } catch (EBotException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}

@DeleteMapping("/{botId}")
public ResponseEntity<?> deleteBot(@PathVariable Long botId) {
    try {
        botService.deleteBot(botId);
        return ResponseEntity.ok().build();
    } catch (EBotException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}

}    