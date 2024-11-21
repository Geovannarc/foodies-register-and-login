package org.example.controller;

import lombok.extern.log4j.Log4j2;
import org.example.dto.UserDTO;
import org.example.dto.UserDetailsDTO;
import org.example.dto.UserLoginDTO;
import org.example.dto.UserResponseDTO;
import org.example.service.LoginService;
import org.example.service.RegisterService;
import org.example.util.JwtUtil;
import org.example.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Log4j2
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBuilder> register(@Validated @RequestBody UserDTO userDTO) {
        UserResponseDTO user;
        try {
            user = registerService.register(userDTO);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ResponseBuilder(null, "Dados inválidos"),
                    HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseBuilder(null, "Erro ao salvar usuário"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseBuilder(user.getToken(), user.getId()),
                HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBuilder> login(@Validated @RequestBody UserLoginDTO userDTO) {
        UserResponseDTO user;
        try {
            user = loginService.login(userDTO.getUsername(), userDTO.getPasswordHash());
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseBuilder(null, "Nome de usuário ou senha inválidos"),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseBuilder(user.getToken(), user.getId()),
                HttpStatus.OK);
    }

    @PostMapping(value = "create-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBuilder> createProfile(@Validated @ModelAttribute UserDetailsDTO user,
                                                         @RequestHeader("Authorization") String token) {
        if (!jwtUtil.validateToken(token, user.getUsername())) {
            log.info("Usuário não autenticado");
            return new ResponseEntity<>(new ResponseBuilder(null, "Usuário não autenticado"),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            registerService.registerUserDetails(user);
        } catch (DataIntegrityViolationException e) {
            log.info("Dados inválidos: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseBuilder(null, "Dados inválidos"),
                    HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.info("Erro ao salvar detalhes do usuário: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseBuilder(null, "Erro ao salvar detalhes do usuário"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Detalhes do usuário salvos com sucesso: {}", user.getUsername());
        return new ResponseEntity<>(new ResponseBuilder(token, null),
                HttpStatus.CREATED);
    }

    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBuilder> validateUser(@RequestHeader("Authorization") String token,
                                                        @RequestParam("username") String username) {
        if (username == null || !jwtUtil.validateToken(token, username)) {
            return new ResponseEntity<>(new ResponseBuilder(null, "Usuário não autenticado"),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseBuilder(null, "Usuário autenticado"),
                HttpStatus.OK);
    }

    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBuilder> logout(@RequestHeader("Authorization") String token,
                                                  @RequestParam("username") String username) {
        if (token == null || !jwtUtil.validateToken(token, username)) {
            return new ResponseEntity<>(new ResponseBuilder(null, "Usuário não autenticado"),
                    HttpStatus.BAD_REQUEST);
        }
        jwtUtil.invalidateToken(username);
        return new ResponseEntity<>(new ResponseBuilder(null, "Usuário deslogado"),
                HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<ResponseBuilder> getUserId(@RequestHeader("Authorization") String token,
                                                     @RequestParam("username") String username) {
        if (token == null || !jwtUtil.validateToken(token, username)) {
            return new ResponseEntity<>(new ResponseBuilder(null, "Usuário não autenticado"),
                    HttpStatus.BAD_REQUEST);
        }
        Long userId = loginService.getUserId(username);
        return new ResponseEntity<>(new ResponseBuilder(null, userId), HttpStatus.OK);
    }
}
