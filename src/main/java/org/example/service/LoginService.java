package org.example.service;

import org.example.dto.UserResponseDTO;

import java.util.List;

public interface LoginService {

    public UserResponseDTO login(final String username, final String password) throws Exception;

    public Long getUserId(final String username);

    List<String> getUsers(String name);
}
