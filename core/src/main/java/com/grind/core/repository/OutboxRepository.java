package com.grind.core.repository;

import com.grind.core.model.OutboxEvent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {

    @Query(
            value = """
                        SELECT * FROM outbox
                        WHERE status = 'NEW'
                        ORDER BY created_at
                        LIMIT :limit
                        FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OutboxEvent> lockBatch(@Param("limit") int limit);

    @Modifying
    @Transactional
    @Query(
            value = """
                        UPDATE outbox
                        SET status = 'SENT',
                            processed_at = now()
                        WHERE id IN (:event_ids) AND status = 'NEW'
                    """,
            nativeQuery = true
    )
    void updateBatch(@Param("event_ids") List<String> eventIds);
}
