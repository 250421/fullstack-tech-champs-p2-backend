package com.revature.nflfantasydraft.Repository;

import org.springframework.stereotype.Repository;

import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.EAuthException;

@Repository
public interface UserRepository {
    Integer create(String userName, String email, String password) throws EAuthException;

    User findByEmailAndPassword(String email, String password) throws EAuthException;

    Integer getCountByEmail(String email);
    
    User findById(Integer userId);
}