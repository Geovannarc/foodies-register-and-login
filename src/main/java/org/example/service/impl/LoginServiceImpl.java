package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.dto.UserDataDTO;
import org.example.dto.UserResponseDTO;
import org.example.model.UserModel;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Log4j2
public class LoginServiceImpl implements org.example.service.LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UserResponseDTO login(String username, final String password) throws Exception {
        username = username.toLowerCase().trim();
        log.info("Logging in user: " + username);
        UserModel user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            String token = jwtUtil.generateToken(username);
            userRepository.updateToken(token, username);
            String id = encodeId(userRepository.getUserId(username));
            log.info("User logged in: " + username);
            return new UserResponseDTO(id, token);
        } else {
            log.error("Failed to login user: " + username);
            throw new Exception("Invalid username or password");
        }
    }

    public Long getUserId(String username) {
        return userRepository.findByUsername(username).getId();
    }

    private static String encodeId(Long id) {
        return Base64.getUrlEncoder().encodeToString(id.toString().getBytes());
    }

    public List<UserDataDTO> getUsers(String name) {
        List<String> users = userRepository.findByName(name);
        List<UserDataDTO> userData = new ArrayList<>();
        users.forEach(user -> userData.add(new UserDataDTO(user.split(",")[0], user.split(",")[1])));
        return userData;
    }
}