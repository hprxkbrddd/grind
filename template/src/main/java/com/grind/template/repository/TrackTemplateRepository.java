package com.grind.template.repository;

import com.grind.template.entity.TrackTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackTemplateRepository extends JpaRepository<TrackTemplate, String> {
}
