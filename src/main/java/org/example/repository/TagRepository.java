package org.example.repository;


import jakarta.transaction.Transactional;
import org.example.model.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagModel, Long> {

    @Modifying
    @Transactional
    @Query(value = "insert ignore into user_tag(id_user, id_tag) " +
            "values (:userId, :tagId)", nativeQuery = true)
    void addTag(Long userId, Integer tagId);
}
