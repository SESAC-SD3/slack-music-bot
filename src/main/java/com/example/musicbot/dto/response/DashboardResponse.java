package com.example.musicbot.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    private long totalSongs;
    private long playedSongs;
    private long pendingSongs;
    private List<UserStats> topContributors;
    private List<SongStats> mostPlayedSongs;
    private List<SongResponse> recentSongs;

    @Getter
    @Builder
    public static class UserStats {
        private String username;
        private long addedCount;
        private long playedCount;
    }

    @Getter
    @Builder
    public static class SongStats {
        private String videoId;
        private String title;
        private long count;
    }
}
