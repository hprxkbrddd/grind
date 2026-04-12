package com.grind.core.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grind.core.dto.entity.TaskDTO;
import com.grind.core.dto.wrap.Reply;
import com.grind.core.enums.CoreMessageType;
import com.grind.core.enums.TaskStatus;
import com.grind.core.model.Task;
import com.grind.core.service.application.TaskService;
import com.grind.core.util.ActionReplyExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskReplyHandlerTest {

    @Mock
    private TaskService taskService;

    private TaskReplyHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TaskReplyHandler(taskService, new ObjectMapper(), new ActionReplyExecutor());
    }

    @Test
    void routeReply_shouldMapChangeTaskFieldsInCorrectOrder() {
        Task task = new Task();
        task.setId("task-1");
        task.setTitle("new-title");
        task.setDescription("new-description");
        task.setStatus(TaskStatus.CREATED);

        when(taskService.changeTask("task-1", "new-title", "new-description"))
                .thenReturn(task);

        Reply<?> reply = handler.routeReply(
                CoreMessageType.CHANGE_TASK,
                "{\"taskId\":\"task-1\",\"title\":\"new-title\",\"description\":\"new-description\"}"
        );

        TaskDTO payload = (TaskDTO) reply.body().payload();

        assertEquals(CoreMessageType.TASK_CHANGED, reply.type());
        assertEquals("new-title", payload.title());
        assertEquals("new-description", payload.description());
        verify(taskService).changeTask("task-1", "new-title", "new-description");
    }

    @Test
    void routeReply_shouldReturnBadRequestForInvalidJson() {
        Reply<?> reply = handler.routeReply(CoreMessageType.CHANGE_TASK, "{");

        assertEquals(CoreMessageType.ERROR, reply.type());
        assertEquals(HttpStatus.BAD_REQUEST, reply.body().status());
    }
}
