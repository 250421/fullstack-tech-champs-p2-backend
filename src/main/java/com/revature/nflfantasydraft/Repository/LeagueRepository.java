package com.revature.nflfantasydraft.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.revature.nflfantasydraft.Entity.League;
import com.revature.nflfantasydraft.Exceptions.EtBadRequestException;
import com.revature.nflfantasydraft.Exceptions.EtResourceNotFoundException;

@Repository
public class LeagueRepository {

    private static final String SQL_CREATE = "INSERT INTO leagues(num_players) VALUES(?)";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM leagues WHERE id = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM leagues";
    private static final String SQL_UPDATE = "UPDATE leagues SET num_players = ?, drafting = ?, current_pick = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM leagues WHERE id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<League> findAll() throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.query(SQL_FIND_ALL, leagueRowMapper);
        } catch (Exception e) {
            throw new EtBadRequestException("Could not fetch data");
        }
    }

    public League findById(Integer id) throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, leagueRowMapper, id);
        } catch (Exception e) {
            throw new EtResourceNotFoundException("League Not Found");
        }
    }

    public Integer create(Integer num_players) throws EtBadRequestException {
        System.out.println("In create league repo");
        try {
            System.out.println("About to connect");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                System.out.println("Got connection");
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, num_players);
                return ps;
            }, keyHolder);

            System.out.println("DATA ADDED TO LEAGUES TABLE");

            // Log the keys returned in KeyHolder for debugging
            System.out.println("Keys in KeyHolder: " + keyHolder.getKeys().get("id"));
            
            // Retrieve the generated id
            Number key = (Number) keyHolder.getKeys().get("id");
            Integer id = key.intValue();

            // Check if id is null, in case something went wrong
            if (id == null) {
                throw new EtBadRequestException("League creation failed: Unable to retrieve id");
            }
            
            return id;
        } catch (Exception e) {
            // Handle exception
            throw new EtBadRequestException("Invalid request: " + e.getMessage());
        }
    }

    public void update(Integer id, League league) throws EtBadRequestException {
        System.out.println("In update league repo");
        try {
            int rowsAffected = jdbcTemplate.update(
                SQL_UPDATE,
                league.getNumPlayers(),
                league.getDrafting(),
                league.getCurrentPick(),
                id
            );
            if (rowsAffected == 0) {
                throw new EtResourceNotFoundException("League not found to update");
            }
        } catch (Exception e) {
            // Handle exception
            throw new EtBadRequestException("Invalid request: " + e.getMessage());
        }
        
    }

    public void removeById(Integer id) throws EtResourceNotFoundException {
        System.out.println("In delete league repo");
        try {
            int rowsAffected = jdbcTemplate.update(SQL_DELETE, id);
            if (rowsAffected == 0) {
                throw new EtResourceNotFoundException("League not found to delete");
            }
        } catch (Exception e) {
            // Handle exception
            throw new EtBadRequestException("Invalid request: " + e.getMessage());
        }
    }

    private final RowMapper<League> leagueRowMapper = ((rs, rowNum) -> {
        return new League(
            rs.getInt("id"),
            rs.getInt("num_players"),
            rs.getBoolean("drafting"),
            rs.getInt("current_pick")
        );
    });
    
}