package com.revature.nflfantasydraft.Controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.nflfantasydraft.Entity.User;
import com.revature.nflfantasydraft.Service.Constants;
import com.revature.nflfantasydraft.Service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(403).body("{\"error\": \"Unauthorized access\"}");
            }

            Optional <User> user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(404).body("{\"error\": \"User not found\"}");
            }

            return ResponseEntity.ok(user); // returns user object as JSON
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Something went wrong\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, Object> userMap) {
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        
        try{
            User user = userService.loginUser(email, password);
            // Map<String, String> map = new HashMap<>();
            // map.put("message", "log In successfully");
            // return new ResponseEntity<>(map, HttpStatus.OK);
            return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if it's truly an exception
        }
    }
    
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Map<String, Object> userMap) {
        System.out.println("TESTING HERE");
        String userName = (String) userMap.get("userName");
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        
        System.out.println("Before calling userService");

        try{
            User user = userService.registerUser(userName, email, password);
            System.out.println("After calling userService");

            // -- W/O Jwt
            // Map<String, String> map = new HashMap<>();
            // map.put("message", "registered successfully");
            // return new ResponseEntity<>(map, HttpStatus.OK);
            // -- END: W/O Jwt

            return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if it's truly an exception
        }
    }

    // --- Help Functions ---

    // Create JWT Token
    private Map<String, String> generateJWTToken(User user) {
        long timestamp = System.currentTimeMillis();

        // Convert the API secret key into a proper signing key
        Key hmacKey = new SecretKeySpec(
            Constants.API_SECRET_KEY.getBytes(StandardCharsets.UTF_8),
            SignatureAlgorithm.HS256.getJcaName()
        );

        String token = Jwts.builder()
            .signWith(hmacKey, SignatureAlgorithm.HS256)
            .setIssuedAt(new Date(timestamp))
            .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
            .claim("userId", user.getUserId())
            .claim("email", user.getEmail())
            .claim("userName", user.getUserName())
            .compact();
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }
}