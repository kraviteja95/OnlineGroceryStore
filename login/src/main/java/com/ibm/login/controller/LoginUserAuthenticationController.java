package com.ibm.login.controller;

import com.ibm.login.exception.UserAlreadyExistsException;
import com.ibm.login.model.Order;
import com.ibm.login.model.User;
import com.ibm.login.service.LoginUserAuthenticationService;
import com.ibm.login.service.LoginUserAuthenticationServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableOAuth2Sso
@RequestMapping("/login")
@CrossOrigin("*")
@Slf4j
public class LoginUserAuthenticationController extends WebSecurityConfigurerAdapter {

    static final long EXPIRATIONTIME = 3000000;
    Map<String, String> map = new HashMap<>();
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LoginUserAuthenticationServiceImpl loginUserAuthenticationServiceImpl;

    private LoginUserAuthenticationService loginUserAuthenticationService;

    public LoginUserAuthenticationController(LoginUserAuthenticationService authenticationService) {
        this.loginUserAuthenticationService = loginUserAuthenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Configuring Spring security access. For /login, /user, and /userinfo, we need
        // authentication.
        // Logout is enabled.
        // Adding csrf token support to this configurer.
        http.authorizeRequests().antMatchers("/login**", "/user", "/userInfo").authenticated().and().logout()
            .logoutSuccessUrl("/").permitAll().and().csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    @RequestMapping("/user")
    public Principal user(Principal principal) {
        // Principal holds the logged in user information.
        // Spring automatically populates this principal object after login.
        return principal;
    }

    @RequestMapping("/userInfo")
    public String userInfo(Principal principal) {
        final OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
        final Authentication authentication = oAuth2Authentication.getUserAuthentication();
        // Manually getting the details from the authentication, and returning them as String.
        return authentication.getDetails().toString();
    }

    @RequestMapping("/userToken")
    public String getUserToken(Principal principal) {
        String tokenValue = null;
        final Authentication authenticationObject = SecurityContextHolder.getContext().getAuthentication();
        final Object userDetailObject = authenticationObject.getDetails();
        System.out.println("Enter userToken: " + userDetailObject);

        final OAuth2AuthenticationDetails userDetails = (OAuth2AuthenticationDetails) userDetailObject;
        tokenValue = userDetails.getTokenValue();
        System.out.println("User tokens: " + tokenValue);

        return tokenValue;
    }

    @RequestMapping("/getTxnToken")
    public String getTxnToken(Principal principal) throws Exception {
        String txnTokenValue = null;
        txnTokenValue = generateTransactionToken(principal);
        System.out.println(" txnTokenValue: " + txnTokenValue);
        return txnTokenValue;
    }

    @RequestMapping("/getCustomUserToken")
    public String getCustomUserToken(Principal principal) throws Exception {
        String userTokenValue = null;
        userTokenValue = generateServiceToken(principal);
        System.out.println(" CustomerUserToken: " + userTokenValue);
        return userTokenValue;
    }

//    Without encoding and decoding
//    @PostMapping(value="/order", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Order> receiveOrder(@RequestBody Order orderRequest) {
//        Order orderResponse = restTemplate.postForEntity("http://localhost:9187/add/order",orderRequest,Order.class).getBody();
//        return new ResponseEntity<Order>(orderResponse, HttpStatus.OK);
//    }

    //With encoding and decoding
    @PostMapping(value = "/orders")
    public ResponseEntity<Order> receiveOrders(@RequestBody String orderRequest) throws Exception {
        log.info("Here's the request object from UI => " + orderRequest);
        String txnToken = generateJwtTokenFromTheIncomingRequest(orderRequest);
        log.info("Encoded the incoming JWT token request from login microservice => " + txnToken);
        Order orderResponse = restTemplate.postForEntity("http://localhost:9187/add/orders", txnToken, Order.class).getBody();
        log.info("Received the response from the Order microservice to the Login microservice => " + orderResponse);
        return new ResponseEntity<Order>(orderResponse, HttpStatus.OK);
    }

    /*
     * Creating a new user.
     * Below are the status codes based on different situations:
     * 1. 201(CREATED) - If the user created successfully.
     * 2. 409(CONFLICT) - If the userId conflicts with any existing user.
     * This handler method should map to the URL "/login/register" using HTTP
     * POST method
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        ResponseEntity<User> result = null;
        try {
            loginUserAuthenticationService.saveUser(user);
            result = new ResponseEntity<User>(HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<String>("This user already exists. Please re-look into it.", HttpStatus.CONFLICT);
        }
        return result;
    }

    /*
     * The username and password should be validated before proceeding ahead with JWT token generation.
     * The user credentials will be validated against the DB entries. The error should be returned in case if the validation is not successful.
     * If credentials are validated successfully, then JWT token will be generated.
     * The token should be returned back to the caller along with the API response.
     * Below are the status codes based on different situations:
     * 1. 200(OK) - If login is successful
     * 2. 401(UNAUTHORIZED) - If login is not successful
     * This handler method should map to the URL "/login" using HTTP
     * POST method
     */

    // Generate JWT token
    public String getToken(String username, String password) throws Exception {

        if (username == null || password == null) {
            throw new ServletException("Please fill your username and password");
        }

        User user = loginUserAuthenticationService.findByUserIdAndPassword(username, password);

        if (user == null) {
            throw new ServletException("Invalid credentials");
        }

        String jwtToken = Jwts.builder().setSubject(username).setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .signWith(SignatureAlgorithm.HS256, "secretkey").compact();

        return jwtToken;
    }

    public String generateUserToken(Principal principal) throws Exception {

        String transactionToken = Jwts.builder().setSubject(principal.getName()).setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .signWith(SignatureAlgorithm.HS256, "secretkey").compact();

        return transactionToken;
    }

    public String generateTransactionToken(Principal principal) throws Exception {

        String transactionToken = Jwts.builder().setSubject(principal.getName()).setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .signWith(SignatureAlgorithm.HS384, "secretkey").compact();

        return transactionToken;
    }

    public String generateJwtTokenFromTheIncomingRequest(String request) throws Exception {
        return Jwts.builder().setSubject(request).setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .signWith(SignatureAlgorithm.HS384, "secretkey").compact();
    }

    public String getClaims(String jwtToken) {
        String[] splitToken = jwtToken.split("\\.");
        String encodedBody = splitToken[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(encodedBody));
        return body;
    }

    public String generateServiceToken(Principal principal) throws Exception {

        String serviceToken = Jwts.builder().setSubject(principal.getName()).setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .signWith(SignatureAlgorithm.HS512, "secretkey").compact();

        return serviceToken;
    }
}