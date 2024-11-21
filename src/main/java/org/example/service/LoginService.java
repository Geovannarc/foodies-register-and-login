package org.example.service;

import org.example.dto.UserResponseDTO;

public interface LoginService {

    public UserResponseDTO login(final String username, final String password) throws Exception;

    public Long getUserId(final String username);
}
