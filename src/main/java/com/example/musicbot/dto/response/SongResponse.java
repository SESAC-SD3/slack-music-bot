package com.example.musicbot.dto.response;

import com.example.musicbot.entity.Song;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SongResponse {

    private Long id;
    private String title;
    private String videoId;
    private String thumbnailUrl;
    private String addedBy;
    private Integer orderIndex;
    private boolean played;
    private LocalDateTime createdAt;

    public static SongResponse from(Song song) {
        return SongResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .videoId(song.getVideoId())
                .thumbnailUrl(song.getThumbnailUrl())
                .addedBy(song.getAddedBy())
                .orderIndex(song.getOrderIndex())
                .played(song.isPlayed())
                .createdAt(song.getCreatedAt())
                .build();
    }
}
