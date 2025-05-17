package com.revature.nflfantasydraft.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.revature.nflfantasydraft.Entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByUserUserId(Integer userId);
    
    @Query("SELECT t FROM Team t WHERE t.user.userId = :userId AND t.teamId = :teamId")
    Team findByUserAndTeamId(Integer userId, Long teamId);

    List<Team> findByLeagueId(Long leagueId);
}   