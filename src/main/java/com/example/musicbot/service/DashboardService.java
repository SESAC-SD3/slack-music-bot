package com.example.musicbot.service;

import com.example.musicbot.dto.response.DashboardResponse;
import com.example.musicbot.dto.response.SongResponse;
import com.example.musicbot.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final SongRepository songRepository;

    public DashboardResponse getDashboardStats() {
        long totalSongs = songRepository.count();
        long playedSongs = songRepository.countByPlayedTrue();
        long pendingSongs = songRepository.countByPlayedFalseAndDeletedFalse();

        // 사용자별 통계
        List<Object[]> addedByUser = songRepository.countByAddedByGrouped();
        List<Object[]> playedByUser = songRepository.countPlayedByAddedByGrouped();

        Map<String, Long> playedMap = new HashMap<>();
        for (Object[] row : playedByUser) {
            playedMap.put((String) row[0], (Long) row[1]);
        }

        List<DashboardResponse.UserStats> topContributors = new ArrayList<>();
        for (int i = 0; i < Math.min(10, addedByUser.size()); i++) {
            Object[] row = addedByUser.get(i);
            String username = (String) row[0];
            Long addedCount = (Long) row[1];
            Long playedCount = playedMap.getOrDefault(username, 0L);

            topContributors.add(DashboardResponse.UserStats.builder()
                    .username(username)
                    .addedCount(addedCount)
                    .playedCount(playedCount)
                    .build());
        }

        // 인기 곡 (가장 많이 추가된)
        List<Object[]> mostAdded = songRepository.findMostAddedSongs();
        List<DashboardResponse.SongStats> mostPlayedSongs = mostAdded.stream()
                .limit(10)
                .map(row -> DashboardResponse.SongStats.builder()
                        .videoId((String) row[0])
                        .title((String) row[1])
                        .count((Long) row[2])
                        .build())
                .collect(Collectors.toList());

        // 최근 추가된 곡
        List<SongResponse> recentSongs = songRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(SongResponse::from)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalSongs(totalSongs)
                .playedSongs(playedSongs)
                .pendingSongs(pendingSongs)
                .topContributors(topContributors)
                .mostPlayedSongs(mostPlayedSongs)
                .recentSongs(recentSongs)
                .build();
    }
}
