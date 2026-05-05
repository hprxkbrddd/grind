package com.grind.template.service.application;

import com.grind.template.dto.CreateTaskTemplateDTO;
import com.grind.template.dto.EditTaskTemplateDTO;
import com.grind.template.entity.TaskTemplate;
import com.grind.template.repository.TaskTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
@RequiredArgsConstructor
public class TaskTemplateService {
    private final TaskTemplateRepository repository;
    private final TrackTemplateService trackTemplateService;

    // TODO Future task-management methods:
    // - getTaskById(String id) with explicit not-found behavior.
    // - deleteTaskTemplate(String id), respecting the parent track template status.
    // - getByTrackIdOrdered(String trackTemplateId): stable ordering by planned day/group/order field.
    // - moveTaskToDay(String taskId, Integer plannedDayOffset).
    // - changeTaskGroup(String taskId, String taskGroup).
    // - replaceTasks(String trackTemplateId, List<CreateTaskTemplateDTO> tasks) for bulk template editing.
    // - reorderTasks(String trackTemplateId, List<String> orderedTaskIds) after adding an order field.
    //
    // TODO Future domain checks:
    // - Ensure a task belongs to the track being edited.
    // - Ensure plannedDayOffset is within the parent track duration.
    // - Define whether tasks can be edited after the parent template is PUBLISHED or ARCHIVED.
    // - Prevent duplicate/conflicting ordering when task ordering is introduced.

    public List<TaskTemplate> getByTrackId(String trackTemplateId){
        return repository.findByTrackTemplate(
                trackTemplateService.getRefById(trackTemplateId)
        );
    }

    public TaskTemplate create(CreateTaskTemplateDTO dto){
        TaskTemplate template = new TaskTemplate();
        template.setId(UUID.randomUUID().toString());
        template.setTitle(dto.title());
        template.setDescription(dto.description());
        template.setTrackTemplate(trackTemplateService.getRefById(dto.trackTemplateId()));
        template.setPlannedDayOffset(dto.plannedDayOffset());
        template.setIsRequired(dto.isRequired());
        template.setTaskGroup(dto.taskGroup());
        template.setEstimatedMinutes(dto.estimatedMinutes());

        return repository.save(template);
    }

    public TaskTemplate edit(@Valid EditTaskTemplateDTO dto) {
        TaskTemplate template = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("No task template with such id: " + dto.id()));

        if (dto.title() != null) {
            template.setTitle(dto.title());
        }
        if (dto.description() != null) {
            template.setDescription(dto.description());
        }
        if (dto.trackTemplateId() != null) {
            template.setTrackTemplate(trackTemplateService.getRefById(dto.trackTemplateId()));
        }
        if (dto.plannedDayOffset() != null) {
            template.setPlannedDayOffset(dto.plannedDayOffset());
        }
        if (dto.isRequired() != null) {
            template.setIsRequired(dto.isRequired());
        }
        if (dto.taskGroup() != null) {
            template.setTaskGroup(dto.taskGroup());
        }
        if (dto.estimatedMinutes() != null) {
            template.setEstimatedMinutes(dto.estimatedMinutes());
        }

        return repository.save(template);
    }

    public Map<String, List<TaskTemplate>> getByTrackIdGrouped(String trackId){
        List<TaskTemplate> taskTemplates = getByTrackId(trackId);
        Map<String, List<TaskTemplate>> res = new HashMap<>();
        for (TaskTemplate t : taskTemplates){
            String taskGroup = t.getTaskGroup();
            res.computeIfAbsent(taskGroup, k -> new ArrayList<>());
            res.get(taskGroup).add(t);
        }

        return res;
    }
}
