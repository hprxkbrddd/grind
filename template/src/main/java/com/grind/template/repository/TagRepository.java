package com.grind.template.repository;

import com.grind.template.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    Optional<Tag> findBySlug(String slug);

    List<Tag> findBySlugIn(List<String> slugs);
}
