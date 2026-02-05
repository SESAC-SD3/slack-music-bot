package com.example.musicbot.slack;

import com.example.musicbot.dto.request.SongAddRequest;
import com.example.musicbot.dto.response.PlayerStateResponse;
import com.example.musicbot.dto.response.SongResponse;
import com.example.musicbot.service.PlayerService;
import com.example.musicbot.service.SongService;
import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackCommandHandler {

    private final App slackApp;
    private final SongService songService;
    private final PlayerService playerService;

    @PostConstruct
    public void registerCommands() {
        // /music 명령어 등록
        slackApp.command("/music", this::handleMusicCommand);
    }

    private Response handleMusicCommand(SlashCommandRequest req, SlashCommandContext ctx) {
        String text = req.getPayload().getText().trim();
        String userId = req.getPayload().getUserName();

        log.info("슬랙 명령어 수신: /music {} (by {})", text, userId);

        if (text.isEmpty()) {
            return ctx.ack(getHelpMessage());
        }

        String[] parts = text.split("\\s+", 2);
        String subCommand = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        try {
            return switch (subCommand) {
                case "add" -> handleAdd(ctx, args, userId);
                case "list" -> handleList(ctx);
                case "play" -> handlePlay(ctx);
                case "pause" -> handlePause(ctx);
                case "next" -> handleNext(ctx);
                case "prev", "previous" -> handlePrevious(ctx);
                case "now" -> handleNow(ctx);
                case "volume" -> handleVolume(ctx, args);
                case "help" -> ctx.ack(getHelpMessage());
                default -> ctx.ack("알 수 없는 명령어입니다. `/music help`로 도움말을 확인하세요.");
            };
        } catch (Exception e) {
            log.error("명령어 처리 중 오류 발생", e);
            return ctx.ack("오류가 발생했습니다: " + e.getMessage());
        }
    }

    private Response handleAdd(SlashCommandContext ctx, String url, String userId) {
        if (url.isEmpty()) {
            return ctx.ack("YouTube URL을 입력해주세요.\n사용법: `/music add [YouTube URL]`");
        }

        SongAddRequest request = new SongAddRequest();
        request.setYoutubeUrl(url);
        request.setAddedBy(userId);

        SongResponse song = songService.addSong(request);
        return ctx.ack(String.format("✅ 곡이 추가되었습니다!\n*%s*\n추가자: %s", song.getTitle(), userId));
    }

    private Response handleList(SlashCommandContext ctx) {
        List<SongResponse> songs = songService.getUnplayedSongs();

        if (songs.isEmpty()) {
            return ctx.ack("📭 플레이리스트가 비어있습니다.\n`/music add [YouTube URL]`로 곡을 추가하세요.");
        }

        StringBuilder sb = new StringBuilder("🎵 *플레이리스트* (").append(songs.size()).append("곡)\n\n");
        int index = 1;
        for (SongResponse song : songs) {
            sb.append(index++).append(". ")
              .append(song.getTitle())
              .append(" (by ").append(song.getAddedBy()).append(")\n");

            if (index > 10) {
                sb.append("... 외 ").append(songs.size() - 10).append("곡");
                break;
            }
        }

        return ctx.ack(sb.toString());
    }

    private Response handlePlay(SlashCommandContext ctx) {
        PlayerStateResponse state = playerService.play();
        return ctx.ack("▶️ 재생을 시작합니다.");
    }

    private Response handlePause(SlashCommandContext ctx) {
        PlayerStateResponse state = playerService.pause();
        return ctx.ack("⏸️ 일시정지되었습니다.");
    }

    private Response handleNext(SlashCommandContext ctx) {
        PlayerStateResponse state = playerService.next();
        if (state.getCurrentSong() != null) {
            return ctx.ack("⏭️ 다음 곡: *" + state.getCurrentSong().getTitle() + "*");
        } else {
            return ctx.ack("⏭️ 플레이리스트가 끝났습니다. 기본 영상을 재생합니다.");
        }
    }

    private Response handlePrevious(SlashCommandContext ctx) {
        PlayerStateResponse state = playerService.previous();
        return ctx.ack("⏮️ 처음부터 다시 재생합니다.");
    }

    private Response handleNow(SlashCommandContext ctx) {
        PlayerStateResponse state = playerService.getState();

        if (state.getCurrentSong() != null) {
            SongResponse song = state.getCurrentSong();
            String status = state.getIsPlaying() ? "▶️ 재생 중" : "⏸️ 일시정지";
            return ctx.ack(String.format("%s\n*%s*\n볼륨: %d%%\n남은 곡: %d곡",
                    status, song.getTitle(), state.getVolume(), state.getRemainingSongs()));
        } else {
            return ctx.ack("현재 재생 중인 곡이 없습니다.");
        }
    }

    private Response handleVolume(SlashCommandContext ctx, String args) {
        if (args.isEmpty()) {
            PlayerStateResponse state = playerService.getState();
            return ctx.ack("🔊 현재 볼륨: " + state.getVolume() + "%");
        }

        try {
            int volume = Integer.parseInt(args);
            PlayerStateResponse state = playerService.setVolume(volume);
            return ctx.ack("🔊 볼륨이 " + volume + "%로 설정되었습니다.");
        } catch (NumberFormatException e) {
            return ctx.ack("볼륨은 0-100 사이의 숫자로 입력해주세요.");
        }
    }

    private String getHelpMessage() {
        return """
            🎵 *Music Bot 사용법*

            `/music add [YouTube URL]` - 곡 추가
            `/music list` - 플레이리스트 보기
            `/music play` - 재생
            `/music pause` - 일시정지
            `/music next` - 다음 곡
            `/music prev` - 이전 곡 (처음부터)
            `/music now` - 현재 재생 정보
            `/music volume [0-100]` - 볼륨 조절
            `/music help` - 도움말
            """;
    }
}
