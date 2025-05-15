package com.revature.nflfantasydraft;

import com.revature.nflfantasydraft.Dto.ApiPlayerDto;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Repository.PlayerRepository;
import com.revature.nflfantasydraft.Service.PlayerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PlayerService playerService;

    private ApiPlayerDto apiPlayerDto;
    private Player playerEntity;

    @BeforeEach
    void setUp() {
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
        // 1. Create test data
        ApiPlayerDto[] apiPlayers = {apiPlayerDto};
        
        // 2. Create a real ResponseEntity (not a mock)
        ResponseEntity<ApiPlayerDto[]> responseEntity = 
            new ResponseEntity<>(apiPlayers, HttpStatus.OK);
        
        // 3. Setup mock behavior - when exchange() is called, return our responseEntity
        when(restTemplate.exchange(
            anyString(), 
            eq(HttpMethod.GET), 
            isNull(), 
            eq(ApiPlayerDto[].class))
        ).thenReturn(responseEntity);
    
        // 4. Mock repository responses
        when(playerRepository.findExistingPlayerIds(anyInt(), anyInt()))
            .thenReturn(Collections.emptySet());
        when(playerRepository.saveAll(anyList()))
            .thenReturn(Collections.singletonList(playerEntity));
    
        // 5. Execute the test
        List<Player> result = playerService.fetchAndSavePlayersForWeek(2024, 1);
    
        // 6. Verify results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dak Prescott", result.get(0).getName());
        
        // 7. Verify mock interactions
        verify(restTemplate).exchange(
            anyString(), 
            eq(HttpMethod.GET), 
            isNull(), 
            eq(ApiPlayerDto[].class));
        verify(playerRepository).findExistingPlayerIds(2024, 1);
        verify(playerRepository).saveAll(anyList());
    }

    @Test
    void fetchAndSavePlayersForWeek_NoNewPlayers() {
        // 1. Create test data
    ApiPlayerDto[] apiPlayers = {apiPlayerDto};
    ResponseEntity<ApiPlayerDto[]> responseEntity = 
        new ResponseEntity<>(apiPlayers, HttpStatus.OK);
    
    // 2. Corrected mock setup with proper parentheses
    when(restTemplate.exchange(
        anyString(), 
        eq(HttpMethod.GET), 
        isNull(), 
        eq(ApiPlayerDto[].class)
    )).thenReturn(responseEntity);  // Properly attached to the when() clause

    // 3. Mock repository response
    when(playerRepository.findExistingPlayerIds(anyInt(), anyInt()))
        .thenReturn(Set.of(apiPlayerDto.getPlayerApiId()));

    // 4. Execute test
    List<Player> result = playerService.fetchAndSavePlayersForWeek(2024, 1);

    // 5. Verify results
    assertTrue(result.isEmpty());
    verify(playerRepository, never()).saveAll(anyList());
    }

    @Test
    void getPlayersBySeasonAndWeek_Success() {
        when(playerRepository.findBySeasonAndWeek(2024, 1))
            .thenReturn(Collections.singletonList(playerEntity));

        List<Player> result = playerService.getPlayersBySeasonAndWeek(2024, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dak Prescott", result.get(0).getName());
    }

    @Test
    void getPlayersByPositionWithTotalPoints_Success() {
        // 1. Create test players
        Player player1 = new Player();
        player1.setPlayerApiId(12345);
        player1.setFantasyPoints(10.0);
        
        Player player2 = new Player();
        player2.setPlayerApiId(12345);
        player2.setFantasyPoints(15.0);
    
        // 2. Create and configure the typed query mock
        @SuppressWarnings("unchecked")
        TypedQuery<Player> typedQueryMock = mock(TypedQuery.class);
        
        // Configure method chaining:
        // - setParameter() should return the mock itself (for chaining)
        // - getResultList() should return our test data
        when(typedQueryMock.setParameter(anyString(), any()))
            .thenReturn(typedQueryMock);
        when(typedQueryMock.getResultList())
            .thenReturn(Arrays.asList(player1, player2));
    
        // 3. Configure entityManager to return our prepared mock
        when(entityManager.createQuery(anyString(), eq(Player.class)))
            .thenReturn(typedQueryMock);
    
        // 4. Execute the test
        List<Player> result = playerService.getPlayersByPositionWithTotalPoints("QB");
    
        // 5. Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // 6. Verify interactions
        verify(typedQueryMock).setParameter(eq("position"), eq("QB"));
        verify(typedQueryMock).getResultList();
    }

    @Test
    void convertToEntity_ValidDto() {
        Player result = playerService.convertToEntity(apiPlayerDto);

        assertNotNull(result);
        assertEquals(12345, result.getPlayerApiId());
        assertEquals("Dak Prescott", result.getName());
        assertEquals("QB", result.getPosition());
        assertEquals(25.7, result.getFantasyPoints());
    }
}