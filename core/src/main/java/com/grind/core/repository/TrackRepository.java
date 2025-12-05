package com.grind.core.repository;

import com.grind.core.model.Track;

import java.util.List;

public interface TrackRepository {

    List<Track> getAllTracks();

    Track getById(String id);

    void save(Track track);

    void deleteTask(String id);
}
