package com.grind.core.repository;

import com.grind.core.model.Sprint;

import java.util.List;

public interface SprintRepository {

    List<Sprint> getAllSprints();

    Sprint getById(String id);

    void save(Sprint sprint);
}
