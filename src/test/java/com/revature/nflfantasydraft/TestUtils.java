package com.revature.nflfantasydraft;

import com.revature.nflfantasydraft.Entity.*;

public class TestUtils {

    public static Team createTestTeam(Long teamId, Long leagueId) {
        Team team = new Team();
        team.setTeamId(teamId);
        team.setLeagueId(leagueId);
        team.setTeamName("Test Team " + teamId);
        
        User user = new User();
        user.setUserId(1);
        team.setUser(user);
        
        return team;
    }

    public static Player createTestPlayer(Integer playerApiId, String position) {
        Player player = new Player();
        player.setPlayerApiId(playerApiId);
        player.setPosition(position);
        player.setName("Player " + playerApiId);
        player.setFantasyPoints(10.0);
        return player;
    }
}