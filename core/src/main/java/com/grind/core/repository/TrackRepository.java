package com.grind.core.repository;

import com.grind.core.model.Track;
import com.grind.core.dto.request.track.TrackWithCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, String> {
    @Query("""
            SELECT new com.grind.core.dto.request.track.TrackWithCount(
                t,
                COUNT(task)
            )
            FROM Track t
            LEFT JOIN t.tasks task
            WHERE t.userId = :userId
            GROUP BY t
            """)
    List<TrackWithCount> findByUserId(String userId);

    @Query("""
            SELECT new com.grind.core.dto.request.track.TrackWithCount(
                t,
                COUNT(task)
            )
            FROM Track t
            LEFT JOIN t.tasks task
            WHERE t.id = :trackId
            """)
    Optional<TrackWithCount> findByIdWithTaskCount(String trackId);

    @Query("""
            SELECT new com.grind.core.dto.request.track.TrackWithCount(
                t,
                COUNT(task)
            )
            FROM Track t
            LEFT JOIN t.tasks task
            GROUP BY t
            """)
    List<TrackWithCount> findAllWithTaskCount();
}
