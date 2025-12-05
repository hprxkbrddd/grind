package com.grind.core.controller;

import com.grind.core.dto.SprintDTO;
import com.grind.core.model.Sprint;
import com.grind.core.request.Sprint.ChangeSprintRequest;
import com.grind.core.request.Sprint.CreateSprintRequest;
import com.grind.core.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("core/v1/sprint")
public class SprintController {

    private final SprintService sprintService;

    @GetMapping
    public ResponseEntity<List<SprintDTO>> sprintIndex(){
        List<Sprint> sprints = sprintService.getAllSprints();
        List<SprintDTO> sprintDTOS = sprints.stream().map(Sprint::mapDTO).collect(Collectors.toList());

        return new ResponseEntity<>(sprintDTOS,
                sprintDTOS.isEmpty()?
                        HttpStatus.NO_CONTENT : HttpStatus.FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintDTO> getById(@PathVariable String id){
        return new ResponseEntity<>(sprintService.getById(id).mapDTO(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SprintDTO> createTask(@RequestBody CreateSprintRequest createSprintRequest){
        Sprint sprint = sprintService.createSprint(createSprintRequest);

        return new ResponseEntity<>(sprint.mapDTO(), HttpStatus.CREATED);
    }

    @PutMapping("/change")
    public void changeSprint(@RequestBody ChangeSprintRequest changeSprintRequest){
        sprintService.changeSprint(changeSprintRequest);
    }

    @DeleteMapping("/delete-sprint/{id}")
    public void deleteSprint(@PathVariable String id){
        sprintService.deleteSprint(id);
    }
}
