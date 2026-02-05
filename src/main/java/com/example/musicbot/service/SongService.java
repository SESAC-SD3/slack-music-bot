package com.example.musicbot.service;

import com.example.musicbot.dto.request.SongAddRequest;
import com.example.musicbot.dto.response.SongResponse;
import com.example.musicbot.entity.Song;

import java.util.List;

public interface SongService {

    SongResponse addSong(SongAddRequest request);

    List<SongResponse> getAllSongs();

    List<SongResponse> getUnplayedSongs();

    Song findById(Long songId);

    SongResponse getSong(Long songId);

    void deleteSong(Long songId);

    SongResponse getNextSong();

    void markAsPlayed(Long songId);

    long countUnplayedSongs();
}
