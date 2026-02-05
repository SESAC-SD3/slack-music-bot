package com.example.musicbot.controller;

import com.example.musicbot.dto.response.PlayerStateResponse;
import com.example.musicbot.service.PlayerService;
import com.example.musicbot.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final SongService songService;

    @GetMapping("/")
    public String player(Model model) {
        PlayerStateResponse state = playerService.getState();
        model.addAttribute("state", state);
        model.addAttribute("songs", songService.getUnplayedSongs());
        return "player";
    }
}
