package com.grind.template.service.application;

import com.grind.template.entity.Tag;
import com.grind.template.entity.TrackCategory;
import com.grind.template.repository.TrackCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackCategoryService {
    private final TrackCategoryRepository repository;

    // TODO Future catalog methods:
    // - createCategory, editCategory, deleteCategory with slug uniqueness validation.
    // - getCategoriesOrdered(): order by sort_order.
    // - resolveAllCategoriesOrThrow(List<String> slugs): fail if any requested slug is missing.
    // - Add usage checks before deleting categories that are attached to templates.

    public List<TrackCategory> getAll(){
        return repository.findAll();
    }

    public TrackCategory getBySlug(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Category with name: " + slug + " is not found"));
    }

    public List<TrackCategory> getBySlug(List<String> slugs) {
        return repository.findBySlugIn(slugs);
    }
}
