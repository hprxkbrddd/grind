package com.grind.core.repository;

import com.grind.core.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, String> {
    List<Sprint> findByTrackId(String trackId);
}
