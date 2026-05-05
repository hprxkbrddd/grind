package com.grind.template.repository;

import com.grind.template.entity.TaskTemplate;
import com.grind.template.entity.TrackTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, String> {
    List<TaskTemplate> findByTrackTemplate(TrackTemplate template);


}
