package com.revature.nflfantasydraft.Service;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.EAuthException;
import com.revature.nflfantasydraft.Repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    
    @Override
    public User loginUser(String email, String password) throws EAuthException {
        if (email != null) email = email.toLowerCase();

        // find user details
        return userRepository.findByEmailAndPassword(email, password);
    }   

    @Override
    public User registerUser(String userName, String email, String password) throws EAuthException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(email != null) email = email.toLowerCase();
        if(!pattern.matcher(email).matches()) {
            throw new EAuthException("Invalid email format");
        }

        // -- Check if email already exists --
        System.out.println("Checking if email is already taken: " + email);
        Integer count = userRepository.getCountByEmail(email);

        System.out.println("Got count: " + count);

        // If email already exists throw error
        if(count> 0) {
            throw new EAuthException("Email already in use");
        }

        // Register user by creating the user doc
        Integer userId = userRepository.create(userName, email, password);

        // Return user iD
        return userRepository.findById(userId);
    }

    @Override
    public User getUserById(int userId) {
        try {
            return userRepository.findById(userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}