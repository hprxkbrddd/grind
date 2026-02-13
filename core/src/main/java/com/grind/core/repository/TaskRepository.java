package com.grind.core.repository;

import com.grind.core.enums.TaskStatus;
import com.grind.core.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findBySprintId(String sprintId);

    List<Task> findByTrackId(String trackId);

    List<Task> findBySprintIdAndStatus(String sprintId, TaskStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
                UPDATE Task t
                SET t.status = :overdue
                WHERE t.plannedDate < :today
                  AND t.status = :planned
            """)
    void expireTasks(@Param("today") LocalDate today,
                     @Param("planned") TaskStatus planned,
                     @Param("overdue") TaskStatus overdue);

    @Query("SELECT t FROM Task t WHERE t.plannedDate < :date AND t.status = :status")
    List<Task> getOverdueWithStatus(@Param("date") LocalDate date,
                                    @Param("status") TaskStatus status
    );
}
