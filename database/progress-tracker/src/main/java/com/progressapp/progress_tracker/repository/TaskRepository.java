package com.progressapp.progress_tracker.repository;

import com.progressapp.progress_tracker.entity.Sprint;
import com.progressapp.progress_tracker.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Найти все задачи спринта
    List<Task> findBySprint(Sprint sprint);

    // Найти задачи по статусу
    List<Task> findByStatus(String status);

    // Найти задачи спринта по статусу
    List<Task> findBySprintAndStatus(Sprint sprint, String status);

    // Найти задачи по приоритету
    List<Task> findByPriority(String priority);
}