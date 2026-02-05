package com.example.musicbot.repository;

import com.example.musicbot.entity.PlayerState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStateRepository extends JpaRepository<PlayerState, Long> {
}
