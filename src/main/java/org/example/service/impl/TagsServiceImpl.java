package org.example.service.impl;

import org.example.model.TagModel;
import org.example.repository.UserRepository;
import org.example.repository.TagRepository;
import org.example.service.TagsService;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagsServiceImpl implements TagsService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<TagModel> getTags() {
        return tagRepository.findAll();
    }

    @Override
    public void addTag(String username, List<Integer> tagsId) {
        try {
            Long userId = userRepository.findByUsername(username).getId();
            for (Integer tagId : tagsId) {
                tagRepository.addTag(userId, tagId);
            }
        } catch (JDBCException e) {
            throw new RuntimeException("Failed to connect to database");
        }

    }


}
