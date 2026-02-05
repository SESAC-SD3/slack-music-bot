package com.example.musicbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SongAddRequest {

    @NotBlank(message = "YouTube URL을 입력해주세요")
    private String youtubeUrl;

    private String addedBy;
}
