package com.example.musicbot.service;

import com.example.musicbot.dto.response.PlayerStateResponse;
import com.example.musicbot.dto.response.SongResponse;
import com.example.musicbot.entity.PlayerState;
import com.example.musicbot.entity.Song;
import com.example.musicbot.exception.BusinessException;
import com.example.musicbot.exception.ErrorCode;
import com.example.musicbot.repository.PlayerStateRepository;
import com.example.musicbot.repository.SongRepository;
import com.example.musicbot.websocket.PlayerWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerServiceImpl implements PlayerService {

    private final PlayerStateRepository playerStateRepository;
    private final SongRepository songRepository;
    private final SongService songService;
    private final PlayerWebSocketHandler webSocketHandler;

    @Value("${musicbot.default-video-url}")
    private String defaultVideoUrl;

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
            "(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})"
    );

    @Override
    public PlayerStateResponse getState() {
        PlayerState state = getOrCreatePlayerState();
        return buildResponse(state);
    }

    @Override
    @Transactional
    public PlayerStateResponse play() {
        PlayerState state = getOrCreatePlayerState();
        state.play();

        webSocketHandler.sendCommand("play", null);
        return buildResponse(state);
    }

    @Override
    @Transactional
    public PlayerStateResponse pause() {
        PlayerState state = getOrCreatePlayerState();
        state.pause();

        webSocketHandler.sendCommand("pause", null);
        return buildResponse(state);
    }

    @Override
    @Transactional
    public PlayerStateResponse next() {
        PlayerState state = getOrCreatePlayerState();

        // 현재 곡을 재생 완료로 표시
        if (state.getCurrentSongId() != null) {
            songService.markAsPlayed(state.getCurrentSongId());
        }

        // 다음 곡 가져오기
        SongResponse nextSong = songService.getNextSong();
        if (nextSong != null) {
            state.updateCurrentSong(nextSong.getId());
            webSocketHandler.sendCommand("play", nextSong.getVideoId());
        } else {
            // 플레이리스트가 비어있으면 기본 영상 재생
            state.updateCurrentSong(null);
            String defaultVideoId = extractVideoId(state.getDefaultVideoUrl());
            webSocketHandler.sendCommand("playDefault", defaultVideoId);
        }

        return buildResponse(state);
    }

    @Override
    @Transactional
    public PlayerStateResponse previous() {
        // 이전 곡 기능은 간단히 현재 곡 처음부터 재생으로 구현
        webSocketHandler.sendCommand("restart", null);
        return getState();
    }

    @Override
    @Transactional
    public PlayerStateResponse setVolume(Integer volume) {
        if (volume < 0 || volume > 100) {
            throw new BusinessException(ErrorCode.INVALID_VOLUME);
        }

        PlayerState state = getOrCreatePlayerState();
        state.updateVolume(volume);

        webSocketHandler.sendCommand("volume", String.valueOf(volume));
        return buildResponse(state);
    }

    @Override
    @Transactional
    public PlayerStateResponse setDefaultVideo(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);

        PlayerState state = getOrCreatePlayerState();
        state.updateDefaultVideoUrl(youtubeUrl);

        return buildResponse(state);
    }

    private PlayerState getOrCreatePlayerState() {
        return playerStateRepository.findById(1L)
                .orElseGet(() -> playerStateRepository.save(
                        PlayerState.builder()
                                .isPlaying(false)
                                .volume(50)
                                .defaultVideoUrl(defaultVideoUrl)
                                .build()
                ));
    }

    private PlayerStateResponse buildResponse(PlayerState state) {
        SongResponse currentSong = null;
        if (state.getCurrentSongId() != null) {
            try {
                currentSong = songService.getSong(state.getCurrentSongId());
            } catch (BusinessException e) {
                // 곡이 삭제된 경우 무시
            }
        }

        long remainingSongs = songService.countUnplayedSongs();
        return PlayerStateResponse.from(state, currentSong, remainingSongs);
    }

    private String extractVideoId(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
