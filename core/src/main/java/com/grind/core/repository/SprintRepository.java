package com.grind.core.repository;

import com.grind.core.dto.request.SprintWithCount;
import com.grind.core.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, String> {

    List<Sprint> findByTrackIdOrderByStartDate(String trackId);

    @Query("""
                SELECT new com.grind.core.dto.request.SprintWithCount(
                    s,
                    COUNT(task),
                    s.track.id
                )
                FROM Sprint s
                LEFT JOIN s.tasks task
                WHERE s.track.id = :trackId
                GROUP BY s
                ORDER BY s.startDate
            """)
    List<SprintWithCount> findByTrackIdOrderByStartDateWithCount(String trackId);

    Optional<Sprint> findByTrack_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String trackId,
            LocalDate date1,
            LocalDate date2
    );
}
