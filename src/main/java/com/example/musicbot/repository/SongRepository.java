package com.example.musicbot.repository;

import com.example.musicbot.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    // 삭제되지 않은 곡만 조회
    List<Song> findByDeletedFalseOrderByOrderIndexAsc();

    // 재생 대기 중인 곡 (삭제되지 않고, 재생되지 않은 곡)
    List<Song> findByPlayedFalseAndDeletedFalseOrderByOrderIndexAsc();

    @Query("SELECT COALESCE(MAX(s.orderIndex), 0) FROM Song s WHERE s.deleted = false")
    Integer findMaxOrderIndex();

    Optional<Song> findFirstByPlayedFalseAndDeletedFalseOrderByOrderIndexAsc();

    long countByPlayedFalseAndDeletedFalse();

    // 대시보드 통계용
    long count();

    long countByPlayedTrue();

    @Query("SELECT s.addedBy, COUNT(s) FROM Song s GROUP BY s.addedBy ORDER BY COUNT(s) DESC")
    List<Object[]> countByAddedByGrouped();

    @Query("SELECT s.addedBy, COUNT(s) FROM Song s WHERE s.played = true GROUP BY s.addedBy ORDER BY COUNT(s) DESC")
    List<Object[]> countPlayedByAddedByGrouped();

    // 최근 추가된 곡
    List<Song> findTop10ByOrderByCreatedAtDesc();

    // 가장 많이 추가된 곡 (videoId 기준)
    @Query("SELECT s.videoId, s.title, COUNT(s) FROM Song s GROUP BY s.videoId, s.title ORDER BY COUNT(s) DESC")
    List<Object[]> findMostAddedSongs();
}
