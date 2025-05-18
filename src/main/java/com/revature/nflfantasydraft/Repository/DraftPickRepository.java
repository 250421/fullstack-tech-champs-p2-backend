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

import com.revature.nflfantasydraft.Entity.DraftPick;
import com.revature.nflfantasydraft.Exceptions.EtBadRequestException;
import com.revature.nflfantasydraft.Exceptions.EtResourceNotFoundException;

@Repository
public class DraftPickRepository {

    private static final String SQL_CREATE = "INSERT INTO draft_picks(league_id, pick_number, team_id) VALUES(?, ?, ?)";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM draft_picks WHERE id = ?";
    private static final String SQL_FIND_BY_PICK_NUMBER = "SELECT * FROM draft_picks WHERE pick_number = ?";
    private static final String SQL_FIND_ALL = "SELECT * FROM draft_picks";
    private static final String SQL_UPDATE = "UPDATE draft_picks SET player_id = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM draft_picks WHERE id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<DraftPick> findAll() throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.query(SQL_FIND_ALL, draftPickRowMapper);
        } catch (Exception e) {
            throw new EtBadRequestException("Could not fetch data");
        }
    }

    public DraftPick findById(Integer id) throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, draftPickRowMapper, id);
        } catch (Exception e) {
            throw new EtResourceNotFoundException("DraftPick Not Found");
        }
    }

    public DraftPick findByPickNumber(Integer pick_number) throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_PICK_NUMBER, draftPickRowMapper, pick_number);
        } catch (Exception e) {
            throw new EtResourceNotFoundException("DraftPick Not Found");
        }
    }

    public Integer create(Integer league_id, Integer pick_number, Integer team_id) throws EtBadRequestException {
        System.out.println("In create draft pick repo");
        try {
            System.out.println("About to connect");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                System.out.println("Got connection");
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, league_id);
                ps.setInt(2, pick_number);
                ps.setInt(3, team_id);
                return ps;
            }, keyHolder);

            System.out.println("DATA ADDED TO DRAFT PICKS TABLE");

            // Log the keys returned in KeyHolder for debugging
            System.out.println("Keys in KeyHolder: " + keyHolder.getKeys().get("id"));
            
            // Retrieve the generated id
            Integer id = (Integer) keyHolder.getKeys().get("id");

            // Check if id is null, in case something went wrong
            if (id == null) {
                throw new EtBadRequestException("DraftPick creation failed: Unable to retrieve id");
            }
            
            return id;
        } catch (Exception e) {
            // Handle exception
            throw new EtBadRequestException("Invalid request: " + e.getMessage());
        }
    }

    public void update(Integer id, DraftPick draftPick) throws EtBadRequestException {
        System.out.println("In update draftPick repo");
        try {
            System.out.println("PlayerID, " + draftPick.getPlayerId());
            int rowsAffected = jdbcTemplate.update(
                SQL_UPDATE,
                draftPick.getPlayerId(),
                id
            );
            if (rowsAffected == 0) {
                throw new EtResourceNotFoundException("DraftPick not found to update");
            }
        } catch (Exception e) {
            // Handle exception
            throw new EtBadRequestException("Invalid request: " + e.getMessage());
        }
        
    }

    public void removeById(Integer id) throws EtResourceNotFoundException {
        System.out.println("In delete draftPick repo");
        try {
            int rowsAffected = jdbcTemplate.update(SQL_DELETE, id);
            if (rowsAffected == 0) {
                throw new EtResourceNotFoundException("DraftPick not found to delete");
            }
        } catch (Exception e) {
            // Handle exception
            throw new EtBadRequestException("Invalid request: " + e.getMessage());
        }
    }

    private final RowMapper<DraftPick> draftPickRowMapper = ((rs, rowNum) -> {
        return new DraftPick(
            rs.getInt("id"),
            rs.getInt("league_id"),
            rs.getInt("pick_number"),
            rs.getInt("team_id"),
            rs.getInt("player_id")
        );
    });
    
}