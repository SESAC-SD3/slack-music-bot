package com.example.musicbot.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_VIDEO_URL(HttpStatus.BAD_REQUEST, "유효하지 않은 YouTube URL입니다."),
    INVALID_VOLUME(HttpStatus.BAD_REQUEST, "볼륨은 0에서 100 사이여야 합니다."),
    PLAYLIST_EMPTY(HttpStatus.BAD_REQUEST, "플레이리스트가 비어있습니다."),

    // 404 Not Found
    SONG_NOT_FOUND(HttpStatus.NOT_FOUND, "곡을 찾을 수 없습니다."),
    PLAYER_STATE_NOT_FOUND(HttpStatus.NOT_FOUND, "플레이어 상태를 찾을 수 없습니다."),

    // 500 Internal Server Error
    PLAYER_NOT_CONNECTED(HttpStatus.INTERNAL_SERVER_ERROR, "플레이어가 연결되어 있지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
