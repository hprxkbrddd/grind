package com.grind.template.service.application;

import com.grind.template.entity.Tag;
import com.grind.template.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository repository;

    // TODO Future catalog methods:
    // - createTag, editTag, deleteTag with slug uniqueness validation.
    // - resolveAllTagsOrThrow(List<String> slugs): fail if any requested slug is missing.
    // - Add usage checks before deleting tags that are attached to templates.

    public List<Tag> getAll() {
        return repository.findAll();
    }

    public Tag getBySlug(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Tag with name: " + slug + " is not found"));
    }

    public List<Tag> getBySlug(List<String> slugs) {
        return repository.findBySlugIn(slugs);
    }
}
