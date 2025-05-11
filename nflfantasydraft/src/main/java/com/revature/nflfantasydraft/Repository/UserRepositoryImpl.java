package com.revature.nflfantasydraft.Repository;



import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.EAuthException;

@Repository
public class UserRepositoryImpl implements UserRepository {
    // Updated SQL queries for PostgreSQL
    private static final String SQL_CREATE = "INSERT INTO users(USERNAME, EMAIL, PASSWORD) VALUES(?, ?, ?) RETURNING USER_ID";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM users WHERE EMAIL = ?";
    private static final String SQL_FIND_BY_ID = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD, ROLE FROM users WHERE USER_ID = ?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT USER_ID, USERNAME, EMAIL, PASSWORD, ROLE FROM users WHERE EMAIL = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(String userName, String email, String password) throws EAuthException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
        try {
            // Simplified PostgreSQL implementation using RETURNING clause
            return jdbcTemplate.queryForObject(
                SQL_CREATE,
                Integer.class,
                userName,
                email,
                hashedPassword
            );
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
public Optional<User> findById(Integer userId) {
    try {
        User user = jdbcTemplate.queryForObject(SQL_FIND_BY_ID, userRowMapper, userId);
        return Optional.ofNullable(user);
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
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