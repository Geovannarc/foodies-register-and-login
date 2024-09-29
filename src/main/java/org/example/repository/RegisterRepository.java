package org.example.repository;

import org.example.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterRepository extends JpaRepository<UserModel, Long> {

    @Query(value = "insert into user (username, email, password_hash, created_at, updated_at, profile_id) " +
            "values (:username, :email, :passwordHash, CURRENT_DATE, CURRENT_DATE, 1)", nativeQuery = true)
    void register(String username, String email, String passwordHash);
}

