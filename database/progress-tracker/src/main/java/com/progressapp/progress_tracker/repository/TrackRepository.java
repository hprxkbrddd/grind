package com.progressapp.progress_tracker.repository;

import com.progressapp.progress_tracker.entity.Track;
import com.progressapp.progress_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {

    // Найти все треки пользователя
    List<Track> findByOwner(User owner);

    // Найти треки по статусу
    List<Track> findByStatus(String status);

    // Найти треки пользователя по статусу
    List<Track> findByOwnerAndStatus(User owner, String status);
}