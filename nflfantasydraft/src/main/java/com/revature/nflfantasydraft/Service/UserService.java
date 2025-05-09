package com.revature.nflfantasydraft.Service;

import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Exceptions.EAuthException;

public interface  UserService {
    User loginUser(String email, String password) throws EAuthException;
    User registerUser(String userName, String email, String password) throws EAuthException;
    User getUserById(int userId);
}