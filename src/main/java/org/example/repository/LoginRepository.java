package org.example.repository;

import org.example.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<UserModel, Long> {

    @Query(value = "select * from user where username = :username", nativeQuery = true)
    public UserModel findByUsername(final String username);

}
