package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.dto.UserDTO;
import org.example.model.UserModel;
import org.example.repository.RegisterRepository;
import org.example.service.RegisterService;
import org.example.util.JwtUtil;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Log4j2
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String register(final UserDTO userDTO) {
        try {
            validateUser(userDTO);
            log.info("Saving user: " + userDTO.getUsername());
            UserModel user = new UserModel();
            user.setUsername(userDTO.getUsername().toLowerCase(Locale.ROOT));
            user.setEmail(userDTO.getEmail());
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));
            registerRepository.save(user);
            log.info("User saved: " + user.getUsername());
            return jwtUtil.generateToken(user.getUsername());
        } catch (JDBCException e) {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    private void validateUser(UserDTO userDTO) {
        if (userDTO.getUsername().isEmpty() || userDTO.getEmail().isEmpty() || userDTO.getPasswordHash().isEmpty()) {
            throw new RuntimeException("Invalid user data");
        }
        if(!userDTO.getEmail().contains("@") || !userDTO.getEmail().contains(".")) {
            throw new RuntimeException("Invalid email");
        }
    }
}
