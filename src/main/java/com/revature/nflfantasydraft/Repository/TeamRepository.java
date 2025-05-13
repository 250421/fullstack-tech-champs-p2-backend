package com.revature.nflfantasydraft.Repository;

import com.revature.nflfantasydraft.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByUserUserId(Integer userId);
    
    @Query("SELECT t FROM Team t WHERE t.user.userId = :userId AND t.teamId = :teamId")
    Team findByUserAndTeamId(Integer userId, Long teamId);
}