package com.example.musicbot.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "player_state")
@NoArgsConstructor
public class PlayerState extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long currentSongId;

    @Column(nullable = false)
    private Boolean isPlaying;

    @Column(nullable = false)
    private Integer volume;

    @Column(length = 500)
    private String defaultVideoUrl;

    @Builder
    public PlayerState(Long currentSongId, Boolean isPlaying, Integer volume, String defaultVideoUrl) {
        this.currentSongId = currentSongId;
        this.isPlaying = isPlaying != null ? isPlaying : false;
        this.volume = volume != null ? volume : 50;
        this.defaultVideoUrl = defaultVideoUrl;
    }

    public void updateCurrentSong(Long songId) {
        this.currentSongId = songId;
    }

    public void play() {
        this.isPlaying = true;
    }

    public void pause() {
        this.isPlaying = false;
    }

    public void updateVolume(Integer volume) {
        this.volume = volume;
    }

    public void updateDefaultVideoUrl(String url) {
        this.defaultVideoUrl = url;
    }
}
