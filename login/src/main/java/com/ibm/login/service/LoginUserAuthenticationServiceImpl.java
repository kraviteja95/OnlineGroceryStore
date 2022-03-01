package com.ibm.login.service;

import com.ibm.login.exception.UserAlreadyExistsException;
import com.ibm.login.exception.UserNotFoundException;
import com.ibm.login.model.User;
import com.ibm.login.repository.LoginUserAuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginUserAuthenticationServiceImpl implements LoginUserAuthenticationService {

    @Autowired
    LoginUserAuthenticationRepository loginUserAuthenticationRepository;

    public LoginUserAuthenticationServiceImpl(LoginUserAuthenticationRepository loginUserAuthenticationRepository) {
        this.loginUserAuthenticationRepository = loginUserAuthenticationRepository;
    }

    /*
     * This method should be used to validate a user using userId and password. Call
     * the corresponding method of Respository interface.
     *
     */
    @Override
    public User findByUserIdAndPassword(String userId, String password) throws UserNotFoundException {
        User user = loginUserAuthenticationRepository.findByUserIdAndUserPassword(userId, password);
        if (user == null) {
            throw new UserNotFoundException("User Does not exist");
        }
        return user;
    }

    /*
     * This method should be used to save a new user.Call the corresponding method
     * of Respository interface.
     */

    @Override
    public boolean saveUser(User user) throws UserAlreadyExistsException {
        boolean save = false;
        User userRes = loginUserAuthenticationRepository.findById(user.getUserId()).orElse(null);
        if (userRes == null) {
            user.setUserAddedDate(LocalDateTime.now());
            loginUserAuthenticationRepository.save(user);
            save = true;
        } else {
            throw new UserAlreadyExistsException("User already exists");
        }
        return save;
    }

    @Override
    public boolean updateUser(User user) throws UserAlreadyExistsException {
        boolean save = false;
        User userRes = loginUserAuthenticationRepository.findById(user.getUserId()).orElse(null);
        if (userRes != null) {
            userRes.setServiceToken(user.getServiceToken());
            userRes.setTransactionToken(user.getTransactionToken());
            userRes.setUserToken(user.getUserToken());
            loginUserAuthenticationRepository.save(userRes);
            save = true;
        } else {
            throw new UserAlreadyExistsException("User does not exist");
        }
        return save;
    }
}
