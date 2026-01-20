package com.grind.core.repository;

import com.grind.core.model.Task;

import java.util.List;

public interface TaskRepository {

    List<Task> getAllTasks();

    Task getByID(String id);

    void save(Task task);

    void completeTask(String taskID);

    void expireTask(String taskID);

    void deleteTask(String taskID);
}
