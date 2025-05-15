package com.revature.nflfantasydraft.Service;
import com.revature.nflfantasydraft.Dto.ApiPlayerDto;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Repository.PlayerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import jakarta.persistence.EntityManager;


import java.util.stream.Collectors;
import java.util.*;

@Service
public class PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private final RestTemplate restTemplate;
    private final PlayerRepository playerRepository;
    private final EntityManager entityManager;

    @Value("${sportsdata.api-key}")
    private String apiKey;

    public PlayerService(RestTemplate restTemplate, PlayerRepository playerRepository,
     EntityManager entityManager) {
        this.restTemplate = restTemplate;
        this.playerRepository = playerRepository;
        this.entityManager = entityManager;
    }

    public List<Player> fetchAndSavePlayersForWeek(int season, int week) {
        String url = String.format(
            "https://api.sportsdata.io/v3/nfl/stats/json/FantasyGameStatsByWeek/%dREG/%d?key=%s",
            season, week, apiKey
        );

        try {
            // 1. First try to fetch players from API
            ResponseEntity<ApiPlayerDto[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                ApiPlayerDto[].class
            );
        
            if (response.getStatusCode().is2xxSuccessful()) {
                ApiPlayerDto[] apiPlayers = response.getBody();
                
                // Add null check for the array itself
                if (apiPlayers == null) {
                    logger.warn("API returned null array for season {} week {}", season, week);
                    return Collections.emptyList();
                }
                
                logger.info("Fetched {} players from API for season {} week {}", apiPlayers.length, season, week);
                
                Set<Integer> existingPlayerIds = playerRepository.findExistingPlayerIds(season, week);
                
                // 3. Process in bulk - filter out existing players
                List<Player> newPlayers = Arrays.stream(apiPlayers)
                    .filter(Objects::nonNull)
                    .filter(dto -> !existingPlayerIds.contains(dto.getPlayerApiId()))
                    .map(this::convertToEntity)
                    .peek(player -> player.setTeamId(determineTeamId(player.getTeam())))
                    .collect(Collectors.toList());
                
                // 4. Only save if there are new players
                if (!newPlayers.isEmpty()) {
                    logger.info("Found {} new players to save", newPlayers.size());
                    return playerRepository.saveAll(newPlayers);
                }
                logger.info("No new players to save");
                return Collections.emptyList();
            }
            logger.error("Failed to fetch players. Status: {}", response.getStatusCode());
            return Collections.emptyList();
        } catch (RestClientException e) {
            logger.error("Error fetching players from API", e);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Unexpected error while fetching players", e);
            return Collections.emptyList();
        }
    }


private Long determineTeamId(String teamAbbreviation) {
// Implement your team ID lookup logic here
return null; // or the actual team ID
}

public List<Player> getPlayersBySeasonAndWeek(int season, int week) {
return playerRepository.findBySeasonAndWeek(season, week);
}

public Player convertToEntity(ApiPlayerDto dto) {
Player player = new Player();
player.setPlayerApiId(dto.getPlayerApiId()); // Make sure to set this!
player.setSeason(dto.getSeason());
player.setWeek(dto.getWeek());
player.setTeam(dto.getTeam());
player.setOpponent(dto.getOpponent());
player.setNumber(dto.getNumber());
player.setName(dto.getName());
player.setPosition(dto.getPosition());
player.setFantasyPoints(dto.getFantasyPoints());
return player;

}


// In PlayerService.java
public List<Player> getPlayersByPositionWithTotalPoints(String position) {
    String query = "SELECT p.playerApiId, p.name, p.team, p.position, " +
                  "SUM(p.fantasyPoints) as totalFantasyPoints " +
                  "FROM Player p " +
                  "WHERE p.position = :position AND p.fantasyPoints > 0 " +
                  "AND p.isDrafted = false " +  // Add this condition
                  "GROUP BY p.playerApiId, p.name, p.team, p.position " +
                  "ORDER BY totalFantasyPoints DESC";
    
    return entityManager.createQuery(query, Player.class)
                      .setParameter("position", position)
                      .getResultList();
}

}