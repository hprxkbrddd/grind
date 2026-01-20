package com.grind.core.repository;

import com.grind.core.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findBySprintId(String sprintId);

    List<Task> findBySprintIdAndStatus(String sprintId, String status);

    @Modifying
    @Query("UPDATE Task t SET t.status='COMPLETED' WHERE t.id = :taskId")
    void completeTask(String taskId);

    @Modifying
    @Query("UPDATE Task t SET t.status='OVERDUE' WHERE t.id = :taskId")
    void expireTask(String taskId);

    @Modifying
    @Query("UPDATE Task t SET t.status='PLANNED' WHERE t.id = :taskId")
    void planTask(String taskId);
}
