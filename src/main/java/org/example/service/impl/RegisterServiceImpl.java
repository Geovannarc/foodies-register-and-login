package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.dto.TagDTO;
import org.example.dto.UserDTO;
import org.example.dto.UserDetailsDTO;
import org.example.model.UserDetailsModel;
import org.example.model.UserModel;
import org.example.repository.UserRepository;
import org.example.service.RegisterService;
import org.example.util.JwtUtil;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
@Log4j2
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private S3Service s3Service;

    @Override
    public String register(final UserDTO userDTO) {
        try {
            validateUser(userDTO);
            log.info("Saving user: " + userDTO.getUsername());
            UserModel user = new UserModel();
            user.setUsername(userDTO.getUsername().toLowerCase(Locale.ROOT).trim());
            user.setDate_birth(userDTO.getDateBirth());
            user.setEmail(userDTO.getEmail().toLowerCase().trim());
            user.setProfileId(1283L);
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));
            user.setToken(jwtUtil.generateToken(userDTO.getUsername()));
            userRepository.save(user);
            log.info("User saved: {}", user.getUsername());
            return user.getToken();
        } catch (JDBCException e) {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    @Override
    public void registerUserDetails(UserDetailsDTO user) {
        try {
            log.info("Salvando detalhes do usuário: {}", user.getUsername());
            String imageURL = s3Service.uploadFile(user.getImage(), "profile-pic-foodies");
            user.setImageURL(imageURL);
            log.info("Imagem salva: {}", imageURL);
            UserDetailsModel userModel = new UserDetailsModel();
            Long userId = userRepository.findByUsername(user.getUsername()).getId();
            userModel.setUserId(userId);
            userModel.setName(user.getName());
            userModel.setBio(user.getBio());
            userModel.setUsername(user.getUsername());
            userModel.setImageURL(user.getImageURL());
            userRepository.registerUserDetails(userModel.getUserId(), userModel.getName(), userModel.getBio(), userModel.getImageURL());
            log.info("User details saved: {}", user.getUsername());
        } catch (JDBCException | IOException e) {
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
