package com.revature.nflfantasydraft.Repository;

import com.revature.nflfantasydraft.Entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {
    // This will automatically provide findAll() method
    List<Bot> findByLeagueId(Long leagueId); // Optional: if you need to filter by league
    Optional<Bot> findByTeamId(Long teamId);
}   