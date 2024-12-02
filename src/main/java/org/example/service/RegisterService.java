package org.example.service;

import org.example.dto.UserDTO;
import org.example.dto.UserDetailsDTO;
import org.example.dto.UserResponseDTO;


public interface RegisterService {

    public UserResponseDTO register(final UserDTO user);

    public void registerUserDetails(final UserDetailsDTO user);

    Object getUserDetails(String username);
}
