package com.revature.nflfantasydraft.Repository;
import com.revature.nflfantasydraft.Entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Set;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByPlayerApiId(Integer playerApiId);

    List<Player> findBySeasonAndWeek(Integer season, Integer week);

     @Query("SELECT p.playerApiId FROM Player p WHERE p.season = :season AND p.week = :week")
    Set<Integer> findExistingPlayerIds(@Param("season") Integer season, @Param("week") Integer week);

    boolean existsByPlayerApiIdAndSeasonAndWeek(Integer playerApiId, Integer season, Integer week);
    
    @Query("SELECT p FROM Player p WHERE p.playerApiId = :playerApiId")
    List<Player> findAllByPlayerApiId(@Param("playerApiId") Integer playerApiId);

    @Query("SELECT p.playerApiId, p.name, p.team, p.position, SUM(p.fantasyPoints) as totalPoints " +
       "FROM Player p " +
       "WHERE p.playerApiId = :playerApiId " +
       "GROUP BY p.playerApiId, p.name, p.team, p.position")
List<Object[]> findPlayerWithTotalPointsByApiId(@Param("playerApiId") Integer playerApiId);


        @Query("SELECT p.playerApiId, p.name, p.team, p.position, " +
        "CAST(SUM(p.fantasyPoints) AS double) as totalPoints " +  // Explicit cast
        "FROM Player p WHERE p.position = :position " +
        "GROUP BY p.playerApiId, p.name, p.team, p.position")
        List<Object[]> findPlayersByPositionWithTotalPoints(@Param("position") String position);


    // Alternative: Get aggregated data directly from DB
    @Query("SELECT p.name, p.team, CAST(SUM(p.fantasyPoints) AS double) as totalPoints " +
       "FROM Player p WHERE p.playerApiId = :playerApiId " +
       "GROUP BY p.name, p.team")
    List<Object[]> findPlayerSummaryByApiId(@Param("playerApiId") Integer playerApiId);
    

    @Query("SELECT p FROM Player p WHERE p.isDrafted = false AND p.position = :position")
    List<Player> findAvailablePlayersByPosition(@Param("position") String position);
    
    @Modifying
    @Query("UPDATE Player p SET p.isDrafted = true WHERE p.playerApiId = :playerApiId")
    void markPlayerAsDrafted(@Param("playerApiId") Integer playerApiId);

    @Query("SELECT p.playerApiId, p.name, p.position, p.fantasyPoints, p.isDrafted " +
           "FROM Player p WHERE p.isDrafted = false")
    List<Object[]> findByIsDraftedFalse();

}
 
 


