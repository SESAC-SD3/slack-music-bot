package com.example.musicbot.service;

import com.example.musicbot.dto.response.PlayerStateResponse;

public interface PlayerService {

    PlayerStateResponse getState();

    PlayerStateResponse play();

    PlayerStateResponse pause();

    PlayerStateResponse next();

    PlayerStateResponse previous();

    PlayerStateResponse setVolume(Integer volume);

    PlayerStateResponse setDefaultVideo(String youtubeUrl);
}
