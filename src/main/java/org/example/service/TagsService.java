package org.example.service;

import org.example.model.TagModel;

import java.util.List;

public interface TagsService {

    public List<TagModel> getTags();

    public void addTag(final String userId, final List<Integer> tagId);
}
