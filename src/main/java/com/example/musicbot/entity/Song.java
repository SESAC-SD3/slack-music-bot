package com.example.musicbot.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "songs")
@NoArgsConstructor
public class Song extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String videoId;

    @Column(length = 500)
    private String thumbnailUrl;

    private String addedBy;

    @Column(nullable = false)
    private Integer orderIndex;

    private boolean played;

    private boolean deleted;

    @Builder
    public Song(String title, String videoId, String thumbnailUrl, String addedBy, Integer orderIndex) {
        this.title = title;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
        this.addedBy = addedBy;
        this.orderIndex = orderIndex;
        this.played = false;
        this.deleted = false;
    }

    public void markAsPlayed() {
        this.played = true;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void updateOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
