package com.revature.nflfantasydraft;

import com.revature.nflfantasydraft.Dto.ApiPlayerDto;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Repository.PlayerRepository;
import com.revature.nflfantasydraft.Service.PlayerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private ApiPlayerDto apiPlayerDto;
    private Player playerEntity;

    @BeforeEach
    void setUp() {
        // Setup test data
        apiPlayerDto = new ApiPlayerDto();
        apiPlayerDto.setPlayerApiId(12345);
        apiPlayerDto.setSeason(2024);
        apiPlayerDto.setWeek(1);
        apiPlayerDto.setTeam("DAL");
        apiPlayerDto.setOpponent("NYG");
        apiPlayerDto.setNumber(4);
        apiPlayerDto.setName("Dak Prescott");
        apiPlayerDto.setPosition("QB");
        apiPlayerDto.setFantasyPoints(25.7);

        playerEntity = new Player();
        playerEntity.setPlayerApiId(12345);
        playerEntity.setSeason(2024);
        playerEntity.setWeek(1);
        playerEntity.setTeam("DAL");
        playerEntity.setOpponent("NYG");
        playerEntity.setNumber(4);
        playerEntity.setName("Dak Prescott");
        playerEntity.setPosition("QB");
        playerEntity.setFantasyPoints(25.7);
    }

    @Test
    void fetchAndSavePlayersForWeek_Success() {
        // Mock API response
        ApiPlayerDto[] apiPlayers = {apiPlayerDto};
        ResponseEntity<ApiPlayerDto[]> responseEntity = 
            new ResponseEntity<>(apiPlayers, HttpStatus.OK);
        
        when(restTemplate.exchange(
            anyString(), 
            eq(HttpMethod.GET), 
            isNull(), 
            eq(ApiPlayerDto[].class))
        ).thenReturn(responseEntity);

        // Mock repository responses
        when(playerRepository.findExistingPlayerIds(anyInt(), anyInt()))
            .thenReturn(Collections.emptySet());
        when(playerRepository.saveAll(anyList()))
            .thenReturn(Collections.singletonList(playerEntity));

        // Test
        List<Player> result = playerService.fetchAndSavePlayersForWeek(2024, 1);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dak Prescott", result.get(0).getName());
        
        verify(restTemplate, times(1))
            .exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(ApiPlayerDto[].class));
        verify(playerRepository, times(1)).findExistingPlayerIds(2024, 1);
        verify(playerRepository, times(1)).saveAll(anyList());
    }

    @Test
void fetchAndSavePlayersForWeek_ApiFailure() {
    // Mock API failure with specific exception
    when(restTemplate.exchange(
        anyString(), 
        eq(HttpMethod.GET), 
        isNull(), 
        eq(ApiPlayerDto[].class))
    ).thenThrow(new RestClientException("API Error"));

    // Test
    List<Player> result = playerService.fetchAndSavePlayersForWeek(2024, 1);

    // Verify
    assertTrue(result.isEmpty(), "Should return empty list on API failure");
    
    // Verify no repository interactions occurred
    verify(playerRepository, never()).findExistingPlayerIds(anyInt(), anyInt());
    verify(playerRepository, never()).saveAll(anyList());
}

    @Test
    void fetchAndSavePlayersForWeek_NoNewPlayers() {
        // Mock API response
        ApiPlayerDto[] apiPlayers = {apiPlayerDto};
        ResponseEntity<ApiPlayerDto[]> responseEntity = 
            new ResponseEntity<>(apiPlayers, HttpStatus.OK);
        
        when(restTemplate.exchange(
            anyString(), 
            eq(HttpMethod.GET), 
            isNull(), 
            eq(ApiPlayerDto[].class))
        ).thenReturn(responseEntity);

        // Mock repository - player already exists
    when(playerRepository.findExistingPlayerIds(anyInt(), anyInt()))
    .thenReturn(Collections.singleton(apiPlayerDto.getPlayerApiId()));

        // Test
        List<Player> result = playerService.fetchAndSavePlayersForWeek(2024, 1);

        // Verify
    assertTrue(result.isEmpty(), "Should return empty list when no new players");
    verify(playerRepository, never()).saveAll(anyList());
    verify(playerRepository, times(1)).findExistingPlayerIds(2024, 1);
    }

    @Test
    void getPlayersBySeasonAndWeek_Success() {
        // Mock repository response
        when(playerRepository.findBySeasonAndWeek(2024, 1))
            .thenReturn(Collections.singletonList(playerEntity));

        // Test
        List<Player> result = playerService.getPlayersBySeasonAndWeek(2024, 1);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dak Prescott", result.get(0).getName());
    }

    @Test
    void convertToEntity_ValidDto() {
        // Setup test data
        ApiPlayerDto dto = new ApiPlayerDto();
        dto.setPlayerApiId(12345);
        dto.setSeason(2024);
        dto.setWeek(1);
        dto.setTeam("DAL");
        dto.setOpponent("NYG");
        dto.setNumber(4);
        dto.setName("Dak Prescott");
        dto.setPosition("QB");
        dto.setFantasyPoints(25.7);

        // Test
        Player result = playerService.convertToEntity(dto);

        // Verify
        assertNotNull(result);
        assertEquals(12345, result.getPlayerApiId());
        assertEquals(2024, result.getSeason());
        assertEquals(1, result.getWeek());
        assertEquals("DAL", result.getTeam());
        assertEquals("NYG", result.getOpponent());
        assertEquals(4, result.getNumber());
        assertEquals("Dak Prescott", result.getName());
        assertEquals("QB", result.getPosition());
        assertEquals(25.7, result.getFantasyPoints());
    }
}