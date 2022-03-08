package com.ibm.order.service;

import com.ibm.order.model.Order;
import com.ibm.order.model.Product;
import com.ibm.order.repository.OrderRepository;
import io.jsonwebtoken.Jwts;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

@Service
public class OrderService {

    KeyPairGenerator keyPairGenerator;
    Cipher cipher;
    KeyPair keyPair;

    public OrderService() throws Exception {
        this.keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        this.cipher = Cipher.getInstance("RSA");
        keyPairGenerator.initialize(4084);
        this.keyPair = this.keyPairGenerator.generateKeyPair();
    }

    @Autowired
    OrderRepository orderRepository;

    public Order addOrder(Order order) {
        List<Product> productList = order.getProducts();
        Order orderRes = orderRepository.save(order);
        productList.forEach(i -> i.setOrderId(orderRes.getOrderId()));
        orderRes.setProducts(productList);
        return orderRepository.save(orderRes);
    }

    public String decodeRequest(String request) throws Exception {
        PrivateKey key = getPrivate("C:\\Users\\002HLV744\\Documents\\Personal\\My trainings at IBM\\Git clones\\OnlineGroceryStore\\keypair\\privateKey");
        cipher.init(Cipher.DECRYPT_MODE, key);
        String decryptedMessage = new String(cipher.doFinal(Base64.decodeBase64(request)), "UTF-8");
        return decryptedMessage;
    }

    public String decodeTheEncodedJwt(String encodedString) {
        String[] splitToken = encodedString.split("\\.");
        String encodedBody = splitToken[1];
        System.out.println("Here's the encodedBody : " +encodedBody);
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(encodedBody));
        System.out.println("Here's the body from the JSON : " +body);
        return body;
    }

    public PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}