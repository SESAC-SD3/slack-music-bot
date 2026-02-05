package com.example.musicbot.controller;

import com.example.musicbot.dto.response.DashboardResponse;
import com.example.musicbot.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardResponse stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "dashboard";
    }

    @GetMapping("/api/dashboard")
    @ResponseBody
    public ResponseEntity<DashboardResponse> getDashboardApi() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}
