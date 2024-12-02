package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "userdetails")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsModel {

    @Id
    private Long id;
    private Long userId;
    private String bio;
    private String imageURL;
    private String name;
    private String username;
    private Long followercount;
    private Long followingcount;
    private Long postcount;

}
