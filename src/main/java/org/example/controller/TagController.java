package org.example.controller;

import lombok.extern.log4j.Log4j2;
import org.example.dto.UserTagsDTO;
import org.example.service.impl.TagsServiceImpl;
import org.example.util.JwtUtil;
import org.example.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Controller
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TagsServiceImpl tagsService;

    @GetMapping("/listAll")
    public ResponseEntity<ResponseBuilder> getTags() {
        var tags = tagsService.getTags();

        return !tags.isEmpty() ? new ResponseEntity<>(new ResponseBuilder(null, tags),
                    HttpStatus.OK) : new ResponseEntity<>(new ResponseBuilder(null, "Erro ao buscar tags"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseBuilder> saveTag(@RequestHeader("Authorization") String token,
                                                   @RequestParam("username") String username,
                                                   @RequestBody List<UserTagsDTO> tagsIds) {
        if (!jwtUtil.validateToken(token, username)) {
            log.info("Usuário não autenticado");
            return new ResponseEntity<>(new ResponseBuilder(null, "Usuário não autenticado"),
                    HttpStatus.BAD_REQUEST);
        }
        List<Integer> tags = tagsIds.stream().map(UserTagsDTO::getId).toList();
        try {
            tagsService.addTag(username, tags);
        } catch (DataIntegrityViolationException e) {
            log.info("Dados inválidos: {}", username);
            return new ResponseEntity<>(new ResponseBuilder(null, "Dados inválidos"),
                    HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.info("Erro ao salvar tags {} do usuário {} ", tagsIds.stream().map(String::valueOf).toList(), username);
            return new ResponseEntity<>(new ResponseBuilder(null, "Erro ao salvar tags"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Tags salvas com sucesso, usuário: {} ", username);
        return new ResponseEntity<>(new ResponseBuilder(null, "Tags salvas com sucesso"),
                HttpStatus.CREATED);
    }

}
