package com.progressapp.progress_tracker.repository;

import com.progressapp.progress_tracker.entity.Sprint;
import com.progressapp.progress_tracker.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SprintRepository extends JpaRepository<Sprint, UUID> {

    // Найти все спринты трека
    List<Sprint> findByTrack(Track track);

    // Найти спринты по статусу
    List<Sprint> findByStatus(String status);

    // Найти активные спринты трека
    List<Sprint> findByTrackAndStatus(Track track, String status);
}