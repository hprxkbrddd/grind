package com.grind.template.repository;

import com.grind.template.entity.TrackCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackCategoryRepository extends JpaRepository<TrackCategory, String> {
    Optional<TrackCategory> findBySlug(String slug);

    List<TrackCategory> findBySlugIn(List<String> slugs);
}
