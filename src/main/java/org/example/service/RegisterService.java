package org.example.service;

import org.example.dto.UserDTO;
import org.example.dto.UserDetailsDTO;


public interface RegisterService {

    public String register(final UserDTO user);

    public void registerUserDetails(final UserDetailsDTO user);

}
