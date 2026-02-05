package com.example.musicbot.repository;

import com.example.musicbot.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findAllByOrderByOrderIndexAsc();

    List<Song> findByPlayedFalseOrderByOrderIndexAsc();

    @Query("SELECT COALESCE(MAX(s.orderIndex), 0) FROM Song s")
    Integer findMaxOrderIndex();

    Optional<Song> findFirstByPlayedFalseOrderByOrderIndexAsc();

    long countByPlayedFalse();
}
