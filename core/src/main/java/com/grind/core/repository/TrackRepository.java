package com.grind.core.repository;

import com.grind.core.model.Track;
import com.grind.core.request.Track.*;

import java.util.List;

public interface TrackRepository {

    List<Track> getAllTracks();

    Track getById(String id);

    void save(Track track);

    void changeName(ChangeTrackNameRequest changeTrackNameRequest);

    void changeDescription(ChangeTrackDescriptionRequest changeTrackDescriptionRequest);

    void changePetId(ChangeTrackPetIdRequest changeTrackPetIdRequest);

    void changeMessagePolicy(ChangeTrackMessagePolicyRequest changeTrackMessagePolicyRequest);

    void deleteTask(String id);
}
