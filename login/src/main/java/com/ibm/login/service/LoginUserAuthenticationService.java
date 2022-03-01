package com.ibm.login.service;

import com.ibm.login.exception.UserAlreadyExistsException;
import com.ibm.login.exception.UserNotFoundException;
import com.ibm.login.model.User;

public interface LoginUserAuthenticationService {

    public User findByUserIdAndPassword(String userId, String password) throws UserNotFoundException;

    boolean saveUser(User user) throws UserAlreadyExistsException;

    boolean updateUser(User user) throws UserAlreadyExistsException;
}
