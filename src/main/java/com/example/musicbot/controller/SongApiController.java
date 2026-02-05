package com.example.musicbot.controller;

import com.example.musicbot.dto.request.SongAddRequest;
import com.example.musicbot.dto.response.SongResponse;
import com.example.musicbot.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongApiController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<SongResponse> addSong(@Valid @RequestBody SongAddRequest request) {
        SongResponse response = songService.addSong(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SongResponse>> getAllSongs() {
        List<SongResponse> songs = songService.getAllSongs();
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/unplayed")
    public ResponseEntity<List<SongResponse>> getUnplayedSongs() {
        List<SongResponse> songs = songService.getUnplayedSongs();
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/{songId}")
    public ResponseEntity<SongResponse> getSong(@PathVariable Long songId) {
        SongResponse song = songService.getSong(songId);
        return ResponseEntity.ok(song);
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long songId) {
        songService.deleteSong(songId);
        return ResponseEntity.noContent().build();
    }
}
