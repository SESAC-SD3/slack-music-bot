package com.example.musicbot.dto.response;

import com.example.musicbot.entity.PlayerState;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerStateResponse {

    private Long currentSongId;
    private SongResponse currentSong;
    private Boolean isPlaying;
    private Integer volume;
    private String defaultVideoUrl;
    private long remainingSongs;

    public static PlayerStateResponse from(PlayerState state, SongResponse currentSong, long remainingSongs) {
        return PlayerStateResponse.builder()
                .currentSongId(state.getCurrentSongId())
                .currentSong(currentSong)
                .isPlaying(state.getIsPlaying())
                .volume(state.getVolume())
                .defaultVideoUrl(state.getDefaultVideoUrl())
                .remainingSongs(remainingSongs)
                .build();
    }
}
