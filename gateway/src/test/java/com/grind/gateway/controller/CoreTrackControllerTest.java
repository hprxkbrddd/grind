package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.core.track.TrackWithCountDTO;
import com.grind.gateway.enums.TrackStatus;
import com.grind.gateway.service.core.CoreTrackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreTrackControllerTest {

    @Mock
    private CoreTrackService trackService;

    @InjectMocks
    private CoreTrackController controller;

    @Test
    void getAllTracks_shouldReturnErrorBodyWhenServiceFails() {
        when(trackService.callGetAllTracks())
                .thenReturn(Body.err("boom", HttpStatus.BAD_REQUEST));

        ResponseEntity<?> response = controller.getAllTracks();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("boom", response.getBody());
    }

    @Test
    void getTrack_shouldReturnPayloadWhenServiceSucceeds() {
        TrackWithCountDTO payload = new TrackWithCountDTO(
                "track-1",
                "Track",
                "Description",
                "pet-1",
                10,
                3L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 10),
                LocalDateTime.of(2026, 1, 1, 9, 0),
                "policy",
                TrackStatus.ACTIVE,
                "user-1"
        );
        doReturn(Body.ok(payload)).when(trackService).callGetTrack("track-1");

        ResponseEntity<?> response = controller.getTrack("track-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(payload, response.getBody());
    }
}
