package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.model.UserModel;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class LoginServiceImpl implements org.example.service.LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String username, final String password) throws Exception {
        username = username.toLowerCase().trim();
        log.info("Logging in user: " + username);
        UserModel user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            String token = jwtUtil.generateToken(username);
            userRepository.updateToken(token, username);
            log.info("User logged in: " + username);
            return token;
        } else {
            log.error("Failed to login user: " + username);
            throw new Exception("Invalid username or password");
        }
    }
}