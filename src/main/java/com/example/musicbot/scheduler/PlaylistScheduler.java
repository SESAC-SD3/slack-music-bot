package com.example.musicbot.scheduler;

import com.example.musicbot.repository.SongRepository;
import com.example.musicbot.repository.PlayerStateRepository;
import com.example.musicbot.websocket.PlayerWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaylistScheduler {

    private final SongRepository songRepository;
    private final PlayerStateRepository playerStateRepository;
    private final PlayerWebSocketHandler webSocketHandler;

    /**
     * 매일 아침 8시(KST)에 플레이리스트 초기화
     * cron = "초 분 시 일 월 요일"
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    @Transactional
    public void resetPlaylistDaily() {
        log.info("플레이리스트 일일 초기화 시작");

        // 모든 곡 삭제
        long deletedCount = songRepository.count();
        songRepository.deleteAll();

        // 현재 재생 중인 곡 초기화
        playerStateRepository.findById(1L).ifPresent(state -> {
            state.updateCurrentSong(null);
        });

        // 플레이어 UI 업데이트
        webSocketHandler.sendCommand("playlistUpdated", null);
        webSocketHandler.sendCommand("playDefault", null);

        log.info("플레이리스트 초기화 완료: {}곡 삭제됨", deletedCount);
    }
}
