package com.grind.core.repository;

import com.grind.core.model.Task;
import com.grind.core.request.Task.ChangeTaskDescriptionRequest;
import com.grind.core.request.Task.ChangeTaskTitleRequest;

import java.util.List;

public interface TaskRepository {

    List<Task> getAllTasks();

    Task getByID(String id);

    void save(Task task);

    void completeTask(String taskID);

    void expireTask(String taskID);

    void changeName(ChangeTaskTitleRequest changeTaskNameRequest);

    void changeDescription(ChangeTaskDescriptionRequest changeTaskDescriptionRequest);

    void deleteTask(String taskID);
}
