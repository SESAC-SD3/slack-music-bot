package com.example.musicbot.slack;

import com.slack.api.bolt.App;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/slack/events")
public class SlackSlashCommandController extends SlackAppServlet {

    public SlackSlashCommandController(App app) {
        super(app);
    }
}
