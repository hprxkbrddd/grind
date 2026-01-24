package com.grind.core.repository;

import com.grind.core.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, String> {
    List<Sprint> findByTrackIdOrderByStartDate(String trackId);

    Optional<Sprint> findByTrack_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String trackId,
            LocalDate date1,
            LocalDate date2
    );
}
