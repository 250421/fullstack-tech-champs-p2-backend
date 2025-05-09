package com.revature.nflfantasydraft.Repository;

import java.sql.PreparedStatement;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.EAuthException;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final String SQL_CREATE = "INSERT INTO USERS(USERNAME, EMAIL, PASSWORD) VALUES(?, ?, ?)";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM USERS WHERE EMAIL = ?";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT * FROM USERS WHERE EMAIL = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @SuppressWarnings("null")
    @Override
    public Integer create(String userName, String email, String password) throws EAuthException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    SQL_CREATE, 
                    new String[] {"USER_ID"}  // Explicitly specify the primary key column
                );
                ps.setString(1, userName);
                ps.setString(2, email);
                ps.setString(3, hashedPassword);
                return ps;
            }, keyHolder);

            // Debug output to inspect returned keys
            Map<String, Object> keys = keyHolder.getKeys();
            System.out.println("Generated keys: " + keys);
            
            // Try multiple ways to get the generated ID
            if (keys != null && keys.containsKey("USER_ID")) {
                return ((Number) keys.get("USER_ID")).intValue();
            } else if (keyHolder.getKey() != null) {
                return keyHolder.getKey().intValue();
            }
            
            throw new EAuthException("Failed to retrieve generated user ID");
        } catch (Exception e) {
            e.printStackTrace();
            throw new EAuthException("Invalid details. Failed to create account: " + e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @Override
    public User findByEmailAndPassword(String email, String password) throws EAuthException {
        try {
            User user = jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, userRowMapper, email);
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw new EAuthException("Invalid email/password");
            }
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new EAuthException("Invalid email/password");
        }
    }

    @Override
    public Integer getCountByEmail(String email) {
        return jdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL, Integer.class, email);
    }

    @Override
    public User findById(Integer userId) {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, userRowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private final RowMapper<User> userRowMapper = ((rs, rowNum) -> {
        return new User(
            rs.getInt("USER_ID"),
            rs.getString("USERNAME"),
            rs.getString("EMAIL"),
            rs.getString("PASSWORD"),
            rs.getString("ROLE")
        );
    });
}