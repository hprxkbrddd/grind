package com.grind.core.service;

import com.grind.core.model.Sprint;
import com.grind.core.model.Task;
import com.grind.core.model.Track;
import com.grind.core.repository.SprintRepository;
import com.grind.core.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;

    public List<Sprint> getAllSprints() {
        return sprintRepository.findAll();
    }

    public Sprint getById(String id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("could not find sprint"));
    }

    public void createSprint(LocalDate startDate, LocalDate endDate, Track track) {
        Sprint sprint = new Sprint();
        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);
        sprint.setTrack(track);
        sprintRepository.save(sprint);
    }

    public List<Sprint> createSprintsForTrack(Track track) {
        List<Sprint> sprints = new ArrayList<>();
        LocalDate start = track.getStartDate();
        LocalDate target = track.getTargetDate();
        Integer sprintLength = track.getSprintLength();

        for (LocalDate d = start; !d.isAfter(target); d = d.plusDays(sprintLength)) {
            LocalDate end = d.plusDays(sprintLength - 1L);
            if (end.isAfter(target)) end = target;
            Sprint s = new Sprint();
            s.setStartDate(d);
            s.setEndDate(end);
            s.setTrack(track);
            sprints.add(s);
        }

        return sprintRepository.saveAll(sprints);
    }

    @Transactional
    public void recreateSprintsForTrack(Track track) {
        // старые спринты (удалим после переназначения задач)
        List<Sprint> oldSprints = sprintRepository.findByTrackId(track.getId());

        // новые спринты
        List<Sprint> newSprints = createSprintsForTrack(track);

        // задачи НЕ из бэклога: у них есть и sprint, и plannedDate (по твоему условию)
        List<Task> plannedTasks = taskRepository.findByTrackId(track.getId()).stream()
                .filter(t -> t.getSprint() != null && t.getPlannedDate() != null)
                .sorted(Comparator.comparing(Task::getPlannedDate))
                .toList();

        // переназначение по plannedDate
        List<Task> toSave = new ArrayList<>(plannedTasks.size());
        for (Task t : plannedTasks) {
            Sprint newSprint = findSprintForDate(newSprints, t.getPlannedDate());

            // если plannedDate внезапно вне диапазона трека — прижимаем к последнему спринту
            if (newSprint == null) {
                newSprint = newSprints.get(newSprints.size() - 1);
            }

            t.setSprint(newSprint);
            toSave.add(t);
        }
        taskRepository.saveAll(toSave);

        // удаляем старые спринты после переназначения задач
        if (!oldSprints.isEmpty()) {
            sprintRepository.deleteAll(oldSprints);
        }
    }

    private Sprint findSprintForDate(List<Sprint> sprints, LocalDate date) {
        for (Sprint s : sprints) {
            boolean geStart = !date.isBefore(s.getStartDate()); // date >= start
            boolean leEnd = !date.isAfter(s.getEndDate());      // date <= end
            if (geStart && leEnd) return s;
        }
        return null;
    }

    public void deleteSprint(String id) {
        sprintRepository.deleteById(id);
    }
}
