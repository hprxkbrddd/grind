package com.grind.core.service;

import com.grind.core.model.Sprint;
import com.grind.core.repository.SprintRepository;
import com.grind.core.repository.TrackRepository;
import com.grind.core.request.Sprint.ChangeSprintRequest;
import com.grind.core.request.Sprint.CreateSprintRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;

    private final TrackRepository trackRepository;

    public List<Sprint> getAllSprints() {
        return sprintRepository.getAllSprints();
    }

    public Sprint getById(String id){ return sprintRepository.getById(id); }

    public Sprint createSprint(CreateSprintRequest createSprintRequest){
        Sprint sprint = new Sprint();
        sprint.setName(createSprintRequest.name());
        sprint.setStartDate(createSprintRequest.startDate());
        sprint.setEndDate(createSprintRequest.endDate());
        sprint.setStatus(createSprintRequest.status());
        sprint.setTrack(trackRepository.getById(createSprintRequest.track_id()));
        sprintRepository.save(sprint);
        return sprint;
    }

    public void changeSprint(ChangeSprintRequest changeSprintRequest){ }

    public void deleteSprint(String id){}
}
