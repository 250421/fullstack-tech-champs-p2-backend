package com.revature.nflfantasydraft.Repository;
import com.revature.nflfantasydraft.Entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Set;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findBySeasonAndWeek(Integer season, Integer week);

     @Query("SELECT p.playerApiId FROM Player p WHERE p.season = :season AND p.week = :week")
    Set<Integer> findExistingPlayerIds(@Param("season") Integer season, @Param("week") Integer week);

    boolean existsByPlayerApiIdAndSeasonAndWeek(Integer playerApiId, Integer season, Integer week);
    
}




