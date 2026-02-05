package com.example.musicbot.service;

import com.example.musicbot.dto.request.SongAddRequest;
import com.example.musicbot.dto.response.SongResponse;
import com.example.musicbot.entity.Song;
import com.example.musicbot.exception.BusinessException;
import com.example.musicbot.exception.ErrorCode;
import com.example.musicbot.entity.PlayerState;
import com.example.musicbot.repository.PlayerStateRepository;
import com.example.musicbot.repository.SongRepository;
import com.example.musicbot.websocket.PlayerWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final PlayerStateRepository playerStateRepository;
    private final PlayerWebSocketHandler webSocketHandler;

    @Value("${musicbot.default-video-url}")
    private String defaultVideoUrl;

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
            "(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})"
    );

    @Override
    @Transactional
    public SongResponse addSong(SongAddRequest request) {
        String videoId = extractVideoId(request.getYoutubeUrl());

        // 추가 전 재생 대기 중인 곡이 있는지 확인
        boolean wasEmpty = songRepository.countByPlayedFalse() == 0;

        Integer maxOrderIndex = songRepository.findMaxOrderIndex();
        int newOrderIndex = maxOrderIndex + 1;

        Song song = Song.builder()
                .title("YouTube Video") // 실제로는 YouTube API로 제목을 가져올 수 있음
                .videoId(videoId)
                .thumbnailUrl("https://img.youtube.com/vi/" + videoId + "/mqdefault.jpg")
                .addedBy(request.getAddedBy())
                .orderIndex(newOrderIndex)
                .build();

        Song savedSong = songRepository.save(song);

        // 플레이어에 플레이리스트 업데이트 알림
        webSocketHandler.sendCommand("playlistUpdated", savedSong.getVideoId());

        // 첫 번째 곡이면 자동 재생
        if (wasEmpty) {
            PlayerState state = getOrCreatePlayerState();
            state.updateCurrentSong(savedSong.getId());
            state.play();
            webSocketHandler.sendCommand("play", savedSong.getVideoId());
        }

        return SongResponse.from(savedSong);
    }

    @Override
    public List<SongResponse> getAllSongs() {
        return songRepository.findAllByOrderByOrderIndexAsc().stream()
                .map(SongResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResponse> getUnplayedSongs() {
        return songRepository.findByPlayedFalseOrderByOrderIndexAsc().stream()
                .map(SongResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public Song findById(Long songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SONG_NOT_FOUND));
    }

    @Override
    public SongResponse getSong(Long songId) {
        Song song = findById(songId);
        return SongResponse.from(song);
    }

    @Override
    @Transactional
    public void deleteSong(Long songId) {
        Song song = findById(songId);
        songRepository.delete(song);
    }

    @Override
    public SongResponse getNextSong() {
        return songRepository.findFirstByPlayedFalseOrderByOrderIndexAsc()
                .map(SongResponse::from)
                .orElse(null);
    }

    @Override
    @Transactional
    public void markAsPlayed(Long songId) {
        Song song = findById(songId);
        song.markAsPlayed();
    }

    @Override
    public long countUnplayedSongs() {
        return songRepository.countByPlayedFalse();
    }

    private String extractVideoId(String url) {
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new BusinessException(ErrorCode.INVALID_VIDEO_URL);
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
}
