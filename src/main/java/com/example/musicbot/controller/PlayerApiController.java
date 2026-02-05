package com.example.musicbot.controller;

import com.example.musicbot.dto.request.DefaultVideoRequest;
import com.example.musicbot.dto.request.VolumeRequest;
import com.example.musicbot.dto.response.PlayerStateResponse;
import com.example.musicbot.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerApiController {

    private final PlayerService playerService;

    @GetMapping("/state")
    public ResponseEntity<PlayerStateResponse> getState() {
        PlayerStateResponse state = playerService.getState();
        return ResponseEntity.ok(state);
    }

    @PostMapping("/play")
    public ResponseEntity<PlayerStateResponse> play() {
        PlayerStateResponse state = playerService.play();
        return ResponseEntity.ok(state);
    }

    @PostMapping("/pause")
    public ResponseEntity<PlayerStateResponse> pause() {
        PlayerStateResponse state = playerService.pause();
        return ResponseEntity.ok(state);
    }

    @PostMapping("/next")
    public ResponseEntity<PlayerStateResponse> next() {
        PlayerStateResponse state = playerService.next();
        return ResponseEntity.ok(state);
    }

    @PostMapping("/previous")
    public ResponseEntity<PlayerStateResponse> previous() {
        PlayerStateResponse state = playerService.previous();
        return ResponseEntity.ok(state);
    }

    @PostMapping("/volume")
    public ResponseEntity<PlayerStateResponse> setVolume(@Valid @RequestBody VolumeRequest request) {
        PlayerStateResponse state = playerService.setVolume(request.getVolume());
        return ResponseEntity.ok(state);
    }

    @PostMapping("/default-video")
    public ResponseEntity<PlayerStateResponse> setDefaultVideo(@Valid @RequestBody DefaultVideoRequest request) {
        PlayerStateResponse state = playerService.setDefaultVideo(request.getYoutubeUrl());
        return ResponseEntity.ok(state);
    }
}
