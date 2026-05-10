package com.grind.template.repository;

import com.grind.template.entity.TrackTemplate;
import com.grind.template.enums.TrackTemplateStatus;
import com.grind.template.enums.TrackVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackTemplateRepository extends JpaRepository<TrackTemplate, String> {
    List<TrackTemplate> findByVisibility(TrackVisibility visibility);
    List<TrackTemplate> findByVisibilityAndStatus(TrackVisibility visibility, TrackTemplateStatus status);
    List<TrackTemplate> findByAuthorId(String authorId);
    List<TrackTemplate> findByCategoryId(String categoryId);
}
