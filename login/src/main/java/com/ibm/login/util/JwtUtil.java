package com.ibm.login.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;

public class JwtUtil {

    private String SECRET_KEY="secret";

    public String generateToken(String userName){
        HashMap<String, Object> claims=new HashMap<String, Object>();
        return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*2))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }
}
