package com.ibm.login.service;

import com.ibm.login.exception.UserAlreadyExistsException;
import com.ibm.login.exception.UserNotFoundException;
import com.ibm.login.model.User;
import com.ibm.login.repository.LoginUserAuthenticationRepository;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;

@Service
public class LoginUserAuthenticationServiceImpl implements LoginUserAuthenticationService {

    @Autowired
    LoginUserAuthenticationRepository loginUserAuthenticationRepository;
    KeyPairGenerator keyPairGenerator;
    Cipher cipher;
    public KeyPair keyPair;

    public LoginUserAuthenticationServiceImpl(LoginUserAuthenticationRepository loginUserAuthenticationRepository) throws Exception {
        this.loginUserAuthenticationRepository = loginUserAuthenticationRepository;
        this.keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        this.cipher = Cipher.getInstance("RSA");
        keyPairGenerator.initialize(4084);
        this.keyPair = this.keyPairGenerator.generateKeyPair();
        writeToFile("C:\\Users\\002HLV744\\Documents\\Personal\\My trainings at IBM\\Git clones\\OnlineGroceryStore\\keypair\\publicKey",
            keyPair.getPublic().getEncoded());
        writeToFile("C:\\Users\\002HLV744\\Documents\\Personal\\My trainings at IBM\\Git clones\\OnlineGroceryStore\\keypair\\privateKey",
            keyPair.getPrivate().getEncoded());
    }

    /*
     * This method should be used to validate a user using userId and password. Call
     * the corresponding method of Respository interface.
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

    public String encodeRequest(String request) throws Exception {
        PublicKey key = keyPair.getPublic();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        String encryptedMessage = Base64.encodeBase64String(cipher.doFinal(request.getBytes("UTF-8")));
        return encryptedMessage;
    }

    public String decodeRequest(String request) throws Exception {
        PrivateKey key = keyPair.getPrivate();
        cipher.init(Cipher.DECRYPT_MODE, key);
        String decryptedMessage = new String(cipher.doFinal(Base64.decodeBase64(request)), "UTF-8");
        return decryptedMessage;
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
}