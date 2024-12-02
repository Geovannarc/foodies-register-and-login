package org.example.repository;

import org.example.model.UserDetailsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDetailRepository extends JpaRepository<UserDetailsModel, Long> {

    @Query(value = "select * from userdetails where username = :username", nativeQuery = true)
    UserDetailsModel getUserDetails(String username);
}
