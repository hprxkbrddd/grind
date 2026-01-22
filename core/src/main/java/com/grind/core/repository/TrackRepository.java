package com.grind.core.repository;

import com.grind.core.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, String> {
    List<Track> findByUserId(String userId);
}
