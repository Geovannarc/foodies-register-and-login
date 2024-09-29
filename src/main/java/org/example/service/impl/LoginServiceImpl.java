package org.example.service.impl;

import org.example.model.UserModel;
import org.example.repository.LoginRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements org.example.service.LoginService {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String username, final String password) throws Exception {
        username = username.toLowerCase();
        UserModel user = loginRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return jwtUtil.generateToken(username);
        } else {
            throw new Exception("Invalid username or password");
        }
    }
}