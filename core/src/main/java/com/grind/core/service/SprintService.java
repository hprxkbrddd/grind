package com.grind.core.service;

import com.grind.core.model.Sprint;
import com.grind.core.repository.SprintRepository;
import com.grind.core.request.Sprint.ChangeSprintEndDate;
import com.grind.core.request.Sprint.ChangeSprintNameRequest;
import com.grind.core.request.Sprint.ChangeSprintStartDate;
import com.grind.core.request.Sprint.ChangeSprintStatus;
import com.grind.core.request.Sprint.CreateSprintRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;

    public List<Sprint> getAllSprints() {
        return sprintRepository.getAllSprints();
    }

    public Sprint getById(String id){ return sprintRepository.getById(id); }

    public Sprint createSprint(CreateSprintRequest createSprintRequest){
        Sprint sprint = new Sprint();
        sprint.setName(createSprintRequest.getName());
        sprint.setStartDate(createSprintRequest.getStartDate());
        sprint.setEndDate(createSprintRequest.getEndDate());
        sprint.setStatus(createSprintRequest.getStatus());
        sprint.setTrack(createSprintRequest.getTrack());
        sprintRepository.save(sprint);
        return sprint;
    }

    public void changeName(ChangeSprintNameRequest changeSprintNameRequest){ }

    public void changeStartDate(ChangeSprintStartDate changeSprintStartDate){ }

    public void changeEndDate(ChangeSprintEndDate changeSprintEndDate){ }

    public void changeStatus(ChangeSprintStatus changeSprintStatus){ }

    public void deleteSprint(String id){}
}
