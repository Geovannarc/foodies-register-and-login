package org.example.repository;

import jakarta.transaction.Transactional;
import org.example.dto.UserDataDTO;
import org.example.model.UserDetailsModel;
import org.example.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    @Query(value = "select * from user where username = :username", nativeQuery = true)
    UserModel findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "insert into userdetails(user_id, name, bio, imageurl, created_at, updated_at, username) " +
            "values (:userId, :name, :bio, :imageURL, CURRENT_DATE, CURRENT_DATE, :username)", nativeQuery = true)
    void registerUserDetails(Long userId, String name, String bio, String imageURL, String username);

    @Modifying
    @Transactional
    @Query(value = "update user set token = :token where username = :username", nativeQuery = true)
    void updateToken(String token, String username);

    @Query(value = "select id from user where username = :username", nativeQuery = true)
    Long getUserId(String username);


    @Query(value = "select user.username,imageurl from user inner join userdetails on userdetails.user_id = user.id " +
            "where user.username like %:name% limit 20", nativeQuery = true)
    List<String> findByName(String name);

}

