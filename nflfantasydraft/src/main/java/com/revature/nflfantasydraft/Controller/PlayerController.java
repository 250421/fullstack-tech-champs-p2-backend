package com.revature.nflfantasydraft.Controller;
import com.revature.nflfantasydraft.Entity.Player;
import com.revature.nflfantasydraft.Service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/fetch/{season}/{week}")
    public List<Player> fetchPlayersForWeek(@PathVariable int season, @PathVariable int week) {
        return playerService.fetchAndSavePlayersForWeek(season, week);
    }

    @GetMapping("/season/{season}/week/{week}")
    public List<Player> getPlayersByWeek(@PathVariable int season, @PathVariable int week) {
        return playerService.getPlayersBySeasonAndWeek(season, week);
    }
}


