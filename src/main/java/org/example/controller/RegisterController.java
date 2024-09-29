package org.example.controller;

import org.example.dto.UserDTO;
import org.example.dto.UserLoginDTO;
import org.example.service.LoginService;
import org.example.service.RegisterService;
import org.example.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<ResponseBuilder> register(@Validated @RequestBody UserDTO userDTO) {
        try {
            registerService.register(userDTO);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ResponseBuilder("Nome de usuário ou email já cadastrado"),
                    HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseBuilder("Erro ao cadastrar usuário"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseBuilder("Usuário cadastrado com sucesso"),
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
}
