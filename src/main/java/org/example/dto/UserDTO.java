package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String dateBirth;
    @NonNull
    private String passwordHash;
    private String token;
}
