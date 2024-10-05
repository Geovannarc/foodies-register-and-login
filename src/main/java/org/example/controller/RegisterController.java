package org.example.controller;

import lombok.extern.log4j.Log4j2;
import org.example.dto.UserDTO;
import org.example.dto.UserDetailsDTO;
import org.example.dto.UserLoginDTO;
import org.example.service.LoginService;
import org.example.service.RegisterService;
import org.example.service.S3Service;
import org.example.util.JwtUtil;
import org.example.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Log4j2
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private S3Service s3Service;

    @PostMapping("/register")
    public ResponseEntity<ResponseBuilder> register(@Validated @RequestBody UserDTO userDTO) {
        String token;
        try {
            token = registerService.register(userDTO);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ResponseBuilder(null),
                    HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseBuilder(null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseBuilder(token),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBuilder> login(@Validated @RequestBody UserLoginDTO userDTO) {
        String token;
        try {
            token = loginService.login(userDTO.getUsername(), userDTO.getPasswordHash());
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseBuilder("Nome de usuário ou senha inválidos"),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseBuilder(token),
                HttpStatus.OK);
    }

    @PostMapping(value = "create-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseBuilder> createProfile(@Validated @ModelAttribute UserDetailsDTO user,
                                                         @RequestHeader("Authorization") String token)
            throws IOException {
        if (!jwtUtil.validateToken(token, user.getUsername())) {
            log.info("Usuário não autenticado");
            return new ResponseEntity<>(new ResponseBuilder(null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            log.info("Salvando detalhes do usuário: " + user.getUsername());
            String imageURL = s3Service.uploadFile(user.getImage(), "profile-pic-foodies");
            user.setImageURL(imageURL);
            log.info("Imagem salva: " + imageURL);
            registerService.registerUserDetails(user);
            log.info("Detalhes do usuário salvos: " + user.getUsername());
        } catch (DataIntegrityViolationException e) {
            log.info("Erro ao salvar detalhes do usuário: " + e.getMessage());
            return new ResponseEntity<>(new ResponseBuilder(null),
                    HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.info("Erro ao salvar detalhes do usuário: " + e.getMessage());
            return new ResponseEntity<>(new ResponseBuilder(null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Detalhes do usuário salvos com sucesso: " + user.getUsername());
        return new ResponseEntity<>(new ResponseBuilder(token),
                HttpStatus.CREATED);
    }
}
