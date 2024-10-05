package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailsDTO {

    private String bio;
    private String imageURL;
    private MultipartFile image;
    private String name;
    private String username;

}
